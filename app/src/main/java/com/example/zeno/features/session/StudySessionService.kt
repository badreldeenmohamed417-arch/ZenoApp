package com.example.zeno.futures.session

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.example.zeno.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val conversationId: String? = null
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
        
        private const val CHANNEL_ID = "zeno_study_session"
        private const val NOTIFICATION_ID = 101
    }

    private var timer: CountDownTimer? = null
    private var player: ExoPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val duration = intent.getIntExtra(EXTRA_DURATION_MINUTES, 25)
                val subject = intent.getStringExtra(EXTRA_SUBJECT) ?: ""
                val soundId = intent.getStringExtra(EXTRA_SOUND_ID) ?: "none"
                val isBreak = intent.getBooleanExtra(EXTRA_IS_BREAK, false)
                val conversationId = intent.getStringExtra(EXTRA_CONVERSATION_ID)
                startSession(duration, subject, soundId, isBreak, conversationId)
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
        return START_NOT_STICKY
    }

    private fun startSession(minutes: Int, subject: String, soundId: String, isBreak: Boolean, conversationId: String?) {
        val millis = minutes * 60 * 1000L
        val phase = if (isBreak) SessionPhase.BREAK else SessionPhase.STUDYING
        
        _sessionState.value = SessionState(
            phase = phase,
            timeLeftMillis = millis,
            totalTimeMillis = millis,
            isPaused = false,
            subjectName = subject,
            soundId = soundId,
            conversationId = conversationId
        )
        
        startForeground(NOTIFICATION_ID, createNotification())
        startTimer(millis)
        
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

    private fun startTimer(millis: Long) {
        timer?.cancel()
        timer = object : CountDownTimer(millis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _sessionState.value = _sessionState.value.copy(timeLeftMillis = millisUntilFinished)
                updateNotification()
            }

            override fun onFinish() {
                _sessionState.value = _sessionState.value.copy(timeLeftMillis = 0)
                handleSessionFinish()
            }
        }.start()
    }

    private fun handleSessionFinish() {
        val currentState = _sessionState.value
        if (currentState.phase == SessionPhase.STUDYING) {
            // Auto switch to break? For now just stop and play sound
            playTransitionSound(R.raw.free_start)
        }
        stopForeground(true)
        stopSelf()
    }

    private fun pauseSession() {
        timer?.cancel()
        player?.pause()
        _sessionState.value = _sessionState.value.copy(isPaused = true)
        updateNotification()
    }

    private fun resumeSession() {
        _sessionState.value = _sessionState.value.copy(isPaused = false)
        player?.play()
        startTimer(_sessionState.value.timeLeftMillis)
    }

    private fun stopSession() {
        timer?.cancel()
        player?.stop()
        player?.release()
        player = null
        _sessionState.value = SessionState()
        stopForeground(true)
        stopSelf()
    }

    private fun skipSession() {
        timer?.onFinish()
    }

    private fun startFocusMusic(soundId: String) {
        val resId = when (soundId) {
            "rain" -> R.raw.rain_sound
            "cafe" -> R.raw.air_plane_captain
            "white_noise" -> R.raw.forest
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
        
        val userManager = com.example.zeno.data.local.UserManager(this)
        val guestUser = getString(R.string.guestUser)
        val userName = userManager.getDisplayName() ?: guestUser
        
        val title = if (state.phase == SessionPhase.BREAK) {
            getString(R.string.notification_break_title, userName)
        } else {
            getString(R.string.notification_focus_title, state.subjectName)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(timeStr)
            .setSmallIcon(R.drawable.zeno_logo)
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
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        timer?.cancel()
        player?.release()
        player = null
        super.onDestroy()
    }
}
