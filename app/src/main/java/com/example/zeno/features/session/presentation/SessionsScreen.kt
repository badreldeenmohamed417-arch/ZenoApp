package com.example.zeno.features.session.presentation

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.zeno.R
import com.example.zeno.core.ui.modifiers.bounceClickable
import com.example.zeno.data.local.UserManager
import com.example.zeno.features.session.SessionPhase
import com.example.zeno.features.session.StudySessionService
import java.util.Locale

import com.example.zeno.data.AppColors

private val DarkBG: Color @Composable get() = AppColors.BG
private val CardBG: Color @Composable get() = AppColors.CardBG
private val CardBorder: Color @Composable get() = AppColors.CardBorder
private val LimeAccent: Color @Composable get() = AppColors.LimeAccent
private val GoldAccent: Color @Composable get() = AppColors.Gold
private val TextWhite: Color @Composable get() = AppColors.TextWhite
private val TextMuted: Color @Composable get() = AppColors.TextMuted

@Composable
fun SessionsScreen(viewModel: SessionsViewModel) {
    val scrollState = rememberScrollState()
    val defaultSubject = stringResource(id = R.string.auto_str_الرياضيات)
    var selectedSubject by remember(defaultSubject) { mutableStateOf(defaultSubject) }
    val context = LocalContext.current
    val sessionState by StudySessionService.sessionState.collectAsState()

    val userManager = remember { UserManager(context) }
    val userSubjects = remember { userManager.getSubjects().map { it.name }.filter { it.isNotBlank() } }
    val defaultSubjectsList = listOf(stringResource(id = R.string.auto_str_الرياضيات), stringResource(id = R.string.auto_str_الفيزياء), stringResource(id = R.string.auto_str_عربي), "English")
    val subjects = if (userSubjects.isNotEmpty()) userSubjects else defaultSubjectsList

    val totalTime = if (sessionState.totalTimeMillis > 0) sessionState.totalTimeMillis else 25 * 60 * 1000L
    val timeLeft = if (sessionState.totalTimeMillis > 0) sessionState.timeLeftMillis else totalTime
    val progress = if (totalTime > 0) 1f - (timeLeft.toFloat() / totalTime.toFloat()) else 0f

    val minutes = (timeLeft / 1000) / 60
    val seconds = (timeLeft / 1000) % 60
    val timeString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBG)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Title
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (sessionState.phase == SessionPhase.BREAK) stringResource(id = R.string.auto_str_وقت_الراحة) else stringResource(id = R.string.auto_str_جلسة_مذاكرة),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (sessionState.phase == SessionPhase.BREAK) stringResource(id = R.string.auto_str_افصل_شوية_واشحن) else stringResource(id = R.string.auto_str_ركز_شوية_وخلص),
                fontSize = 13.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Big Circular Countdown Timer
        Box(
            modifier = Modifier.size(220.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF2C2F38),
                strokeWidth = 16.dp,
                strokeCap = StrokeCap.Round
            )
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = if (sessionState.phase == SessionPhase.BREAK) GoldAccent else LimeAccent,
                strokeWidth = 16.dp,
                strokeCap = StrokeCap.Round
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeString,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (sessionState.phase == SessionPhase.BREAK) stringResource(id = R.string.auto_str_وقت_الراحة) else stringResource(id = R.string.auto_str_وقت_التركيز),
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Timer Control Buttons Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Cancel / Stop Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(CardBG)
                    .clickable { 
                        sessionState.sessionId?.let { viewModel.completeSession(it) }
                        context.startService(
                            Intent(context, StudySessionService::class.java).apply {
                                action = StudySessionService.ACTION_STOP
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = TextWhite,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Play / Pause Primary Button
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(if (sessionState.phase == SessionPhase.BREAK) GoldAccent else LimeAccent)
                    .bounceClickable { 
                        if (sessionState.phase == SessionPhase.IDLE) {
                            // Call API to start session, then start service
                            viewModel.startSession(selectedSubject) { newSessionId ->
                                ContextCompat.startForegroundService(
                                    context,
                                    Intent(context, StudySessionService::class.java).apply {
                                        action = StudySessionService.ACTION_START
                                        putExtra(StudySessionService.EXTRA_DURATION_MINUTES, 25)
                                        putExtra(StudySessionService.EXTRA_SUBJECT, selectedSubject)
                                        putExtra(StudySessionService.EXTRA_SOUND_ID, "white_noise")
                                        putExtra(StudySessionService.EXTRA_SESSION_ID, newSessionId)
                                    }
                                )
                            }
                        } else if (sessionState.isPaused) {
                            context.startService(
                                Intent(context, StudySessionService::class.java).apply {
                                    action = StudySessionService.ACTION_RESUME
                                }
                            )
                        } else {
                            context.startService(
                                Intent(context, StudySessionService::class.java).apply {
                                    action = StudySessionService.ACTION_PAUSE
                                }
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Reset Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(CardBG)
                    .clickable { 
                        sessionState.sessionId?.let { viewModel.completeSession(it) }
                        context.startService(
                            Intent(context, StudySessionService::class.java).apply {
                                action = StudySessionService.ACTION_STOP
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = TextWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Stats Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(CardBG)
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${sessionState.totalTimeMillis / 60000}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(id = R.string.auto_str_دقيقة_تركيز),
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(CardBG)
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "0",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(id = R.string.auto_str_سؤال_سألته),
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        }



        Spacer(modifier = Modifier.height(30.dp))
    }
}
