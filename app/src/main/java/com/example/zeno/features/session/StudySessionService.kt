package com.example.zeno.features.session

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.example.zeno.R
import com.example.zeno.MainActivity
import com.example.zeno.core.data.EncryptedAuthStorageImpl
import com.example.zeno.core.network.AuthInterceptor
import com.example.zeno.core.network.RetrofitClient
import com.example.zeno.core.network.TokenAuthenticator
import com.example.zeno.data.local.UserManager
import com.example.zeno.features.session.data.SessionApi
import com.example.zeno.features.session.data.repository.SessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

enum class SessionPhase {
    IDLE, STUDYING, BREAK
}

data class SessionState(
    val phase: SessionPhase = SessionPhase.IDLE,
    val timeLeftMillis: Long = 0,
    val totalTimeMillis: Long = 0,
    val isPaused: Boolean = false,
    val subjectName: String = "",
    val soundId: String = "none",
    val conversationId: String? = null,
    val sessionId: String? = null,
    val loopCount: Int = 1
)

@OptIn(UnstableApi::class)
class StudySessionService : Service() {

    companion object {
        private val _sessionState = MutableStateFlow(SessionState())
        val sessionState = _sessionState.asStateFlow()

        const val ACTION_START = "START"
        const val ACTION_PAUSE = "PAUSE"
        const val ACTION_RESUME = "RESUME"
        const val ACTION_STOP = "STOP"
        const val ACTION_SKIP = "SKIP"
        const val ACTION_UPDATE_CHAT = "UPDATE_CHAT"

        const val EXTRA_DURATION_MINUTES = "DURATION_MINUTES"
        const val EXTRA_SUBJECT = "SUBJECT"
        const val EXTRA_SOUND_ID = "SOUND_ID"
        const val EXTRA_IS_BREAK = "IS_BREAK"
        const val EXTRA_CONVERSATION_ID = "CONVERSATION_ID"
        const val EXTRA_SESSION_ID = "SESSION_ID"
        
        private const val CHANNEL_ID = "zeno_study_session"
        private const val NOTIFICATION_ID = 101
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var isServiceActive = false
    private var player: ExoPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    private fun getHomeCacheManager(): com.example.zeno.features.home.data.HomeCacheManager {
        return org.koin.java.KoinJavaComponent.getKoin().get()
    }

    private fun getSessionRepository(): SessionRepository {
        val authStorage = EncryptedAuthStorageImpl(this)
        val authInterceptor = AuthInterceptor(authStorage)
        val tokenAuthenticator = TokenAuthenticator(authStorage, this, RetrofitClient.MAIN_SERVER_BASE_URL)
        val retrofit = RetrofitClient.createMainServerRetrofit(authInterceptor, tokenAuthenticator)
        val api = retrofit.create(SessionApi::class.java)
        return SessionRepository(api, this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val duration = intent.getIntExtra(EXTRA_DURATION_MINUTES, 25)
                val subject = intent.getStringExtra(EXTRA_SUBJECT) ?: ""
                val soundId = intent.getStringExtra(EXTRA_SOUND_ID) ?: "none"
                val isBreak = intent.getBooleanExtra(EXTRA_IS_BREAK, false)
                val conversationId = intent.getStringExtra(EXTRA_CONVERSATION_ID)
                val sessionId = intent.getStringExtra(EXTRA_SESSION_ID)
                startSession(duration, subject, soundId, isBreak, conversationId, sessionId)
            }
            ACTION_PAUSE -> pauseSession()
            ACTION_RESUME -> resumeSession()
            ACTION_STOP -> stopSession()
            ACTION_SKIP -> skipSession()
            ACTION_UPDATE_CHAT -> {
                val conversationId = intent.getStringExtra(EXTRA_CONVERSATION_ID)
                _sessionState.value = _sessionState.value.copy(conversationId = conversationId)
            }
        }
        return START_STICKY
    }

    private fun startSession(minutes: Int, subject: String, soundId: String, isBreak: Boolean, conversationId: String?, sessionId: String?) {
        if (isServiceActive) return
        
        isServiceActive = true
        val millis = minutes * 60 * 1000L
        val phase = if (isBreak) SessionPhase.BREAK else SessionPhase.STUDYING
        
        _sessionState.value = SessionState(
            phase = phase,
            timeLeftMillis = millis,
            totalTimeMillis = millis,
            isPaused = false,
            subjectName = subject,
            soundId = soundId,
            conversationId = conversationId,
            sessionId = sessionId,
            loopCount = 1
        )
        
        startForeground(NOTIFICATION_ID, createNotification())
        
        serviceScope.launch {
            val repo = getSessionRepository()
            repo.syncPendingSessions()
            if (sessionId.isNullOrBlank() && phase == SessionPhase.STUDYING) {
                val res = repo.startSession(subject)
                if (res.isSuccess) {
                    val createdId = res.getOrNull()?.sessionId
                    if (createdId != null) {
                        _sessionState.value = _sessionState.value.copy(sessionId = createdId)
                    }
                }
            }
            startTimerLoop()
        }
        
        if (phase == SessionPhase.STUDYING) {
            playTransitionSound(R.raw.work_start) {
                if (soundId != "none") {
                    startFocusMusic(soundId)
                }
            }
        } else {
            playTransitionSound(R.raw.free_start)
        }
    }

    private suspend fun startTimerLoop() {
        while (isServiceActive) {
            delay(1000)
            val currentState = _sessionState.value
            if (currentState.isPaused) continue

            val newTime = currentState.timeLeftMillis - 1000
            
            if (newTime <= 0) {
                handlePhaseSwitch()
            } else {
                _sessionState.value = currentState.copy(timeLeftMillis = newTime)
                updateNotification()
            }
        }
    }

    private fun handlePhaseSwitch() {
        val currentState = _sessionState.value
        if (currentState.phase == SessionPhase.STUDYING) {
            val sid = currentState.sessionId
            val elapsedMillis = currentState.totalTimeMillis - currentState.timeLeftMillis
            val minutesSpent = (elapsedMillis / 60000L).coerceAtLeast(1).toInt()
            if (!sid.isNullOrBlank()) {
                serviceScope.launch {
                    val repo = getSessionRepository()
                    repo.completeSession(sid, minutesSpent)
                    repo.syncPendingSessions()
                }
            }

            // Switch to Break
            val isLongBreak = currentState.loopCount % 3 == 0 && currentState.loopCount != 1
            val breakMinutes = if (isLongBreak) 30 else 5
            val millis = breakMinutes * 60 * 1000L
            
            _sessionState.value = currentState.copy(
                phase = SessionPhase.BREAK,
                timeLeftMillis = millis,
                totalTimeMillis = millis
            )
            playTransitionSound(R.raw.free_start)
        } else {
            // Switch to Study
            val millis = 25 * 60 * 1000L
            _sessionState.value = currentState.copy(
                phase = SessionPhase.STUDYING,
                timeLeftMillis = millis,
                totalTimeMillis = millis,
                loopCount = currentState.loopCount + 1
            )
            playTransitionSound(R.raw.work_start) {
                if (currentState.soundId != "none") {
                    startFocusMusic(currentState.soundId)
                }
            }
        }
        updateNotification()
    }

    private fun pauseSession() {
        player?.pause()
        _sessionState.value = _sessionState.value.copy(isPaused = true)
        updateNotification()
    }

    private fun resumeSession() {
        _sessionState.value = _sessionState.value.copy(isPaused = false)
        player?.play()
    }

    private fun stopSession() {
        val currentState = _sessionState.value
        val sid = currentState.sessionId
        val elapsedMillis = currentState.totalTimeMillis - currentState.timeLeftMillis
        val minutesSpent = (elapsedMillis / 60000L).coerceAtLeast(1).toInt()

        if (!sid.isNullOrBlank() && currentState.phase == SessionPhase.STUDYING) {
            val repo = getSessionRepository()
            serviceScope.launch {
                repo.completeSession(sid, minutesSpent)
                repo.syncPendingSessions()
                getHomeCacheManager().addLocalStudyMinutes(minutesSpent)
            }
        }

        isServiceActive = false
        player?.stop()
        player?.release()
        player = null
        _sessionState.value = SessionState()
        stopForeground(true)
        stopSelf()
    }

    private fun skipSession() {
        handlePhaseSwitch()
    }

    private fun startFocusMusic(soundId: String) {
        val resId = when (soundId) {
            "nature", "forest", "white_noise" -> R.raw.forest
            "rain" -> R.raw.rain_sound
            "airplane", "cafe" -> R.raw.air_plane_captain
            else -> return
        }

        if (player == null) {
            player = ExoPlayer.Builder(this).build()
        }

        player?.apply {
            stop()
            clearMediaItems()
            val uri = RawResourceDataSource.buildRawResourceUri(resId)
            setMediaItem(MediaItem.fromUri(uri))
            repeatMode = Player.REPEAT_MODE_ONE
            prepare()
            play()
        }
    }

    private fun playTransitionSound(resId: Int, onFinished: (() -> Unit)? = null) {
        if (player == null) {
            player = ExoPlayer.Builder(this).build()
        }

        player?.apply {
            stop()
            clearMediaItems()
            val uri = RawResourceDataSource.buildRawResourceUri(resId)
            setMediaItem(MediaItem.fromUri(uri))
            repeatMode = Player.REPEAT_MODE_OFF
            
            val listener = object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        removeListener(this)
                        onFinished?.invoke()
                    }
                }
            }
            addListener(listener)
            
            prepare()
            play()
        }
    }

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Zeno Study Session",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val state = _sessionState.value
        val timeStr = formatTime(state.timeLeftMillis)
        
        val title = if (state.phase == SessionPhase.BREAK) {
            "Break Time"
        } else {
            "Focus Session: ${state.subjectName}"
        }

        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(timeStr)
            .setContentIntent(openAppPendingIntent)
            .setSmallIcon(R.drawable.ic_zeno_logo)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, createNotification())
    }

    private fun formatTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        isServiceActive = false
        serviceScope.cancel()
        player?.release()
        player = null
        super.onDestroy()
    }
}
