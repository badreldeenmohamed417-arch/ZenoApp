package com.example.zeno.futures.session

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.zeno.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SessionPhase {
    IDLE, STUDYING, BREAK
}

data class SessionState(
    val phase: SessionPhase = SessionPhase.IDLE,
    val timeLeftMillis: Long = 0,
    val totalTimeMillis: Long = 0,
    val isPaused: Boolean = false,
    val subjectName: String = "",
    val conversationId: String? = null
)

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
        const val EXTRA_IS_BREAK = "IS_BREAK"
        const val EXTRA_CONVERSATION_ID = "CONVERSATION_ID"
        
        private const val CHANNEL_ID = "zeno_study_session"
        private const val NOTIFICATION_ID = 101
    }

    private var timer: CountDownTimer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val duration = intent.getIntExtra(EXTRA_DURATION_MINUTES, 25)
                val subject = intent.getStringExtra(EXTRA_SUBJECT) ?: ""
                val isBreak = intent.getBooleanExtra(EXTRA_IS_BREAK, false)
                val conversationId = intent.getStringExtra(EXTRA_CONVERSATION_ID)
                startSession(duration, subject, isBreak, conversationId)
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

    private fun startSession(minutes: Int, subject: String, isBreak: Boolean, conversationId: String?) {
        val millis = minutes * 60 * 1000L
        val phase = if (isBreak) SessionPhase.BREAK else SessionPhase.STUDYING
        
        _sessionState.value = SessionState(
            phase = phase,
            timeLeftMillis = millis,
            totalTimeMillis = millis,
            isPaused = false,
            subjectName = subject,
            conversationId = conversationId
        )
        
        startForeground(NOTIFICATION_ID, createNotification())
        startTimer(millis)
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
                // In a real app, we might automatically switch to break or trigger a sound
                stopForeground(true)
                stopSelf()
            }
        }.start()
    }

    private fun pauseSession() {
        timer?.cancel()
        _sessionState.value = _sessionState.value.copy(isPaused = true)
        updateNotification()
    }

    private fun resumeSession() {
        _sessionState.value = _sessionState.value.copy(isPaused = false)
        startTimer(_sessionState.value.timeLeftMillis)
    }

    private fun stopSession() {
        timer?.cancel()
        _sessionState.value = SessionState()
        stopForeground(true)
        stopSelf()
    }

    private fun skipSession() {
        // Logic to skip current phase
        timer?.onFinish()
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
        val title = if (state.phase == SessionPhase.BREAK) "وقت الراحة" else "وقت التركيز: ${state.subjectName}"

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
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}
