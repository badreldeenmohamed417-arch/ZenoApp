package com.example.zeno.futures.session

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zeno.R
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors
import com.example.zeno.data.local.UserManager
import com.example.zeno.data.model.server.Subject
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudySessionScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val sessionState by StudySessionService.sessionState.collectAsStateWithLifecycle()
    var showExitDialog by remember { mutableStateOf(false) }
    var showChatSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val handleBack = {
        if (sessionState.phase != SessionPhase.IDLE) {
            showExitDialog = true
        } else {
            onBack()
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(txt("exitSessionTitle"), fontWeight = FontWeight.Bold) },
            text = { Text(txt("exitSessionMessage")) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    context.startService(Intent(context, StudySessionService::class.java).apply { action = StudySessionService.ACTION_STOP })
                    onBack()
                }) {
                    Text(txt("Continue"), color = AppColors.Danger)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(txt("cancel"))
                }
            },
            containerColor = AppColors.Surface,
            shape = RoundedCornerShape(18.dp)
        )
    }

    if (showChatSheet) {
        ModalBottomSheet(
            onDismissRequest = { showChatSheet = false },
            sheetState = sheetState,
            containerColor = AppColors.BG,
            dragHandle = null
        ) {
            ChatDropUp(
                conversationId = sessionState.conversationId,
                onConversationCreated = { id ->
                    context.startService(Intent(context, StudySessionService::class.java).apply {
                        action = StudySessionService.ACTION_UPDATE_CHAT
                        putExtra(StudySessionService.EXTRA_CONVERSATION_ID, id)
                    })
                },
                timeLeftStr = formatTime(sessionState.timeLeftMillis)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(AppColors.BG)) {
        when (sessionState.phase) {
            SessionPhase.IDLE -> SessionSetupView(context, userManager, handleBack)
            SessionPhase.STUDYING -> SessionActiveView(context, sessionState, handleBack, onAskZeno = { showChatSheet = true })
            SessionPhase.BREAK -> SessionBreakView(context, sessionState)
            // Summary will be another state or just a different view here
        }
    }
}

@Composable
fun SessionSetupView(context: Context, userManager: UserManager, onBack: () -> Unit) {
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var selectedDuration by remember { mutableIntStateOf(25) }
    var selectedSound by remember { mutableStateOf("none") }

    val subjects = remember { userManager.getSubjects() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = txt("newStudySession"),
                fontSize = 23.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AppColors.TextPrimary
            )
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, contentDescription = null, tint = AppColors.TextMuted)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(txt("whatToStudyToday"), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.TextMuted)
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                SubjectChip(
                    name = txt("unspecified"),
                    isSelected = selectedSubject == null,
                    onClick = { selectedSubject = null }
                )
            }
            items(subjects) { subject ->
                SubjectChip(
                    name = subject.name,
                    isSelected = selectedSubject?.id == subject.id,
                    onClick = { selectedSubject = subject }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(txt("sessionDuration"), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.TextMuted)
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(15, 25, 50).forEach { duration ->
                DurationChip(
                    minutes = duration,
                    isSelected = selectedDuration == duration,
                    onClick = { selectedDuration = duration },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(txt("focusSound"), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.TextMuted)
        Spacer(modifier = Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SoundCard("none", "🎧", txt("noSound"), selectedSound == "none", { selectedSound = it }, Modifier.weight(1f))
                SoundCard("rain", "🌧️", txt("soundRain"), selectedSound == "rain", { selectedSound = it }, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SoundCard("cafe", "☕", txt("soundCafe"), selectedSound == "cafe", { selectedSound = it }, Modifier.weight(1f))
                SoundCard("white_noise", "🌊", txt("soundWhiteNoise"), selectedSound == "white_noise", { selectedSound = it }, Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(22.dp))

        Button(
            onClick = {
                val intent = Intent(context, StudySessionService::class.java).apply {
                    action = StudySessionService.ACTION_START
                    putExtra(StudySessionService.EXTRA_DURATION_MINUTES, selectedDuration)
                    putExtra(StudySessionService.EXTRA_SUBJECT, selectedSubject?.name ?: context.getString(R.string.unspecified))
                    putExtra(StudySessionService.EXTRA_SOUND_ID, selectedSound)
                }
                context.startService(intent)
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
        ) {
            Text(txt("startSession"), color = AppColors.AccentInk, fontWeight = FontWeight.Bold)
        }
        
        Text(
            text = txt("sessionHint"),
            fontSize = 12.sp,
            color = AppColors.TextFaint,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp).fillMaxWidth()
        )
    }
}

@Composable
fun SubjectChip(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) AppColors.AccentSoft else AppColors.Surface)
            .border(1.5.dp, if (isSelected) AppColors.Accent else AppColors.UnfocusedBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(text = name, color = if (isSelected) AppColors.Accent else AppColors.TextPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun DurationChip(minutes: Int, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) AppColors.AccentSoft else AppColors.Surface)
            .border(1.5.dp, if (isSelected) AppColors.Accent else AppColors.UnfocusedBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = minutes.toString(), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = if (isSelected) AppColors.Accent else AppColors.TextPrimary)
            Text(text = txt("minutes"), fontSize = 11.5.sp, color = if (isSelected) AppColors.Accent else AppColors.TextMuted)
        }
    }
}

@Composable
fun SoundCard(id: String, icon: String, name: String, isSelected: Boolean, onClick: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) AppColors.AccentSoft else AppColors.Surface)
            .border(1.5.dp, if (isSelected) AppColors.Accent else AppColors.UnfocusedBorder, RoundedCornerShape(12.dp))
            .clickable { onClick(id) }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(if (isSelected) AppColors.Accent else AppColors.SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 16.sp, color = if (isSelected) AppColors.AccentInk else AppColors.TextPrimary)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isSelected) AppColors.Accent else AppColors.TextPrimary)
    }
}

@Composable
fun SessionActiveView(context: Context, state: SessionState, onClose: () -> Unit, onAskZeno: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(AppColors.SurfaceVariant).padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(state.subjectName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.TextMuted)
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(34.dp).border(1.dp, AppColors.UnfocusedBorder, CircleShape).background(AppColors.SurfaceVariant, CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp), tint = AppColors.TextMuted)
            }
        }

        Spacer(modifier = Modifier.height(60.dp))

        // Timer Ring
        Box(contentAlignment = Alignment.Center) {
            val progress = if (state.totalTimeMillis > 0) state.timeLeftMillis.toFloat() / state.totalTimeMillis else 0f
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(220.dp),
                color = AppColors.SurfaceVariant2,
                strokeWidth = 12.dp,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(220.dp),
                color = AppColors.Accent,
                strokeWidth = 12.dp,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatTime(state.timeLeftMillis),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.TextPrimary
                )
                Text(txt("focusTime"), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextMuted)
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            IconButton(
                onClick = { /* Reset logic if needed */ },
                modifier = Modifier.size(52.dp).border(1.dp, AppColors.UnfocusedBorder, CircleShape).background(AppColors.Surface, CircleShape)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = AppColors.TextPrimary)
            }
            
            FloatingActionButton(
                onClick = {
                    val action = if (state.isPaused) StudySessionService.ACTION_RESUME else StudySessionService.ACTION_PAUSE
                    context.startService(Intent(context, StudySessionService::class.java).apply { this.action = action })
                },
                containerColor = AppColors.Accent,
                contentColor = AppColors.AccentInk,
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(24.dp))
            }

            IconButton(
                onClick = { context.startService(Intent(context, StudySessionService::class.java).apply { action = StudySessionService.ACTION_SKIP }) },
                modifier = Modifier.size(52.dp).border(1.dp, AppColors.UnfocusedBorder, CircleShape).background(AppColors.Surface, CircleShape)
            ) {
                Icon(Icons.Default.SkipNext, contentDescription = null, tint = AppColors.TextPrimary)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Sound indicator
        if (state.soundId != "none") {
            val soundType = when (state.soundId) {
                "rain" -> txt("soundRain")
                "cafe" -> txt("soundCafe")
                "white_noise" -> txt("soundWhiteNoise")
                else -> ""
            }
            val soundName = txt("sound_label", soundType)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AppColors.Surface)
                    .border(1.dp, AppColors.UnfocusedBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = txt("soundPlaying", soundName),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextMuted
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // FAB Chat
        Button(
            onClick = onAskZeno,
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Surface),
            modifier = Modifier.height(52.dp).border(1.dp, AppColors.UnfocusedBorder, RoundedCornerShape(26.dp)),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Orb(modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(txt("askZeno"), color = AppColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
            }
        }
    }
}

@Composable
fun SessionBreakView(context: Context, state: SessionState) {
    Column(
        modifier = Modifier.fillMaxSize().padding(34.dp).background(
            Brush.radialGradient(
                colors = listOf(AppColors.GoldSoft, Color.Transparent),
                radius = 1000f
            )
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Orb(modifier = Modifier.size(64.dp), isGold = true)
        Spacer(modifier = Modifier.height(20.dp))
        Text(txt("breakTime"), fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
        Text(txt("breakDescription"), fontSize = 13.5.sp, color = AppColors.TextMuted, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(14.dp))
        Text(text = formatTime(state.timeLeftMillis), fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.Gold)
        
        Spacer(modifier = Modifier.height(26.dp))
        Button(
            onClick = { context.startService(Intent(context, StudySessionService::class.java).apply { action = StudySessionService.ACTION_SKIP }) },
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.SurfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(txt("skipBreak"), color = AppColors.TextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun Orb(modifier: Modifier = Modifier, isGold: Boolean = false) {
    Image(
        painter = painterResource(id = R.drawable.zeno_ball),
        contentDescription = "Zeno Ball",
        modifier = modifier.clip(CircleShape)
    )
}

fun formatTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
