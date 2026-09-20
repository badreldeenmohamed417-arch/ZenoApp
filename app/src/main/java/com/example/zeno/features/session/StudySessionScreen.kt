package com.example.zeno.features.session

import com.example.zeno.core.txtStr

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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zeno.R
import com.example.zeno.core.data.EncryptedAuthStorageImpl
import com.example.zeno.core.network.AuthInterceptor
import com.example.zeno.core.network.RetrofitClient
import com.example.zeno.core.network.TokenAuthenticator
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors
import com.example.zeno.data.local.UserManager
import com.example.zeno.data.model.server.Subject
import com.example.zeno.features.session.data.StudyPlanApi
import com.example.zeno.features.session.data.repository.StudyPlanRepository
import kotlinx.coroutines.delay
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
            title = { Text(stringResource(R.string.exitSessionConfirmTitle), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.exitSessionConfirmMessage)) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    context.startService(Intent(context, StudySessionService::class.java).apply { action = StudySessionService.ACTION_STOP })
                    onBack()
                }) {
                    Text(stringResource(R.string.exitSession), color = AppColors.Danger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(stringResource(R.string.cancel_button))
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
    var selectedSubjectName by remember { mutableStateOf<String?>(null) }
    var selectedDuration by remember { mutableIntStateOf(25) }
    var selectedSound by remember { mutableStateOf("none") }

    val currentLang = remember { userManager.getLanguage() }
    var planSubjects by remember { mutableStateOf<List<String>>(emptyList()) }
    var isFetchingPlan by remember { mutableStateOf(false) }

    val studyPlanRepo = remember {
        val authStorage = EncryptedAuthStorageImpl(context)
        val authInterceptor = AuthInterceptor(authStorage)
        val tokenAuth = TokenAuthenticator(authStorage, context, RetrofitClient.MAIN_SERVER_BASE_URL)
        val retrofit = RetrofitClient.createMainServerRetrofit(authInterceptor, tokenAuth)
        val api = retrofit.create(StudyPlanApi::class.java)
        StudyPlanRepository(api)
    }

    LaunchedEffect(Unit) {
        var retries = 0
        while (retries < 3 && planSubjects.isEmpty()) {
            isFetchingPlan = true
            val result = studyPlanRepo.getCurrentStudyPlan()
            if (result.isSuccess) {
                val items = result.getOrNull()?.items
                if (!items.isNullOrEmpty()) {
                    val extracted = items.map { it.getLocalizedSubject(currentLang) }
                        .filter { it.isNotBlank() }
                        .distinct()
                    if (extracted.isNotEmpty()) {
                        planSubjects = extracted
                        break
                    }
                }
            }
            retries++
            if (planSubjects.isEmpty()) {
                delay(1000)
            }
        }
        if (planSubjects.isEmpty()) {
            val defaultList = if (currentLang == "ar") {
                listOf("الرياضيات", context.txtStr("auto_str_الفيزياء"), "اللغة الإنجليزية", "اللغة العربية", "الكيمياء")
            } else {
                listOf("Math", "Physics", "English", "Arabic", "Chemistry")
            }
            planSubjects = defaultList
        }
        isFetchingPlan = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.newStudySession),
                fontSize = 23.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AppColors.TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(stringResource(R.string.sessionDuration), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.TextMuted)
        Spacer(modifier = Modifier.height(10.dp))
        val configRepository = com.example.zeno.core.config.data.repository.ConfigRepository(
            com.example.zeno.core.network.RetrofitClient.createMainServerRetrofit(
                com.example.zeno.core.network.AuthInterceptor(com.example.zeno.core.data.EncryptedAuthStorageImpl(context)),
                com.example.zeno.core.network.TokenAuthenticator(com.example.zeno.core.data.EncryptedAuthStorageImpl(context), context, com.example.zeno.core.network.RetrofitClient.MAIN_SERVER_BASE_URL)
            ).create(com.example.zeno.core.config.data.ConfigApi::class.java)
        )
        val config = remember { configRepository.getConfig() }
        val durations = config?.studyDurations ?: listOf(15, 25, 50)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            durations.forEach { duration ->
                DurationChip(
                    minutes = duration,
                    isSelected = selectedDuration == duration,
                    onClick = { selectedDuration = duration },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(stringResource(R.string.focusSound), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.TextMuted)
        Spacer(modifier = Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SoundCard("none", "🎧", stringResource(R.string.noSound), selectedSound == "none", { selectedSound = it }, Modifier.weight(1f))
                SoundCard("nature", "🌿", stringResource(R.string.soundNature), selectedSound == "nature", { selectedSound = it }, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SoundCard("rain", "🌧️", stringResource(R.string.soundRain), selectedSound == "rain", { selectedSound = it }, Modifier.weight(1f))
                SoundCard("airplane", "✈️", stringResource(R.string.soundAirplane), selectedSound == "airplane", { selectedSound = it }, Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(22.dp))

        Button(
            onClick = {
                val intent = Intent(context, StudySessionService::class.java).apply {
                    action = StudySessionService.ACTION_START
                    putExtra(StudySessionService.EXTRA_DURATION_MINUTES, selectedDuration)
                    putExtra(StudySessionService.EXTRA_SUBJECT, selectedSubjectName ?: context.getString(R.string.unspecified))
                    putExtra(StudySessionService.EXTRA_SOUND_ID, selectedSound)
                }
                context.startService(intent)
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
        ) {
            Text(stringResource(R.string.startSession), color = AppColors.AccentInk, fontWeight = FontWeight.Bold)
        }
        
        Text(
            text = stringResource(R.string.sessionHint),
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
            Text(text = stringResource(R.string.minutes), fontSize = 11.5.sp, color = if (isSelected) AppColors.Accent else AppColors.TextMuted)
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
        // Top Subject Chip (No 'X' button)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(AppColors.SurfaceVariant)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = if (state.subjectName.isNotBlank()) state.subjectName else stringResource(R.string.unspecified),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextMuted
            )
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
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.focusTime),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(44.dp))

        // Exactly 2 Control Buttons Row: [Pause/Resume Square Button] & [Door Exit Button]
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Square Pause / Resume Toggle Button
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppColors.Accent)
                    .clickable {
                        val action = if (state.isPaused) StudySessionService.ACTION_RESUME else StudySessionService.ACTION_PAUSE
                        context.startService(Intent(context, StudySessionService::class.java).apply { this.action = action })
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (state.isPaused) stringResource(R.string.resume) else stringResource(R.string.pause),
                    tint = AppColors.AccentInk,
                    modifier = Modifier.size(28.dp)
                )
            }

            // 2. Door Exit Button (Stop & Leave)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppColors.Surface)
                    .border(1.5.dp, AppColors.UnfocusedBorder, RoundedCornerShape(16.dp))
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = stringResource(R.string.exitSession),
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Sound indicator
        if (state.soundId != "none") {
            val soundType = when (state.soundId) {
                "nature", "forest" -> stringResource(R.string.soundNature)
                "rain" -> stringResource(R.string.soundRain)
                "airplane", "cafe" -> stringResource(R.string.soundAirplane)
                else -> ""
            }
            val soundName = stringResource(R.string.soundPlaying, soundType)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AppColors.Surface)
                    .border(1.dp, AppColors.UnfocusedBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = soundName,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextMuted
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))

        // FAB Chat Button
        Button(
            onClick = onAskZeno,
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Surface),
            modifier = Modifier
                .height(52.dp)
                .border(1.dp, AppColors.UnfocusedBorder, RoundedCornerShape(26.dp)),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Orb(modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.askZeno),
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
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
        contentDescription = null,
        modifier = modifier.clip(CircleShape)
    )
}

fun formatTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
