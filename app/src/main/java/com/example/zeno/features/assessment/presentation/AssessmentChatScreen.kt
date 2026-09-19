package com.example.zeno.features.assessment.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.R
import com.example.zeno.core.ThinkingIndicator
import com.example.zeno.core.config.data.repository.ConfigRepository
import com.example.zeno.core.theme.CardFun
import com.example.zeno.data.AppColors
import com.example.zeno.data.local.UserManager
import com.example.zeno.features.assessment.data.AssessmentChatMessage
import com.example.zeno.features.assessment.data.LearningProfile
import com.example.zeno.features.assessment.data.MessageSender
import com.example.zeno.features.session.Orb
import com.example.zeno.features.session.data.repository.StudyPlanRepository

@Composable
fun AssessmentChatScreen(
    studyPlanRepository: StudyPlanRepository,
    configRepository: ConfigRepository,
    onAssessmentComplete: () -> Unit
) {
    val context = LocalContext.current
    val viewModel = remember { AssessmentChatViewModel(context, studyPlanRepository, configRepository) }
    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val learningProfile by viewModel.learningProfile.collectAsState()
    val isGeneratingPlan by viewModel.isGeneratingPlan.collectAsState()

    val listState = rememberLazyListState()

    // Auto scroll to bottom when new messages appear or typing state changes
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AssessmentHeader()
            },
            containerColor = AppColors.BG
        ) { paddingValues ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        if (message.isProfileCard) {
                            LearningProfileCard(
                                profile = learningProfile,
                                onStartClicked = {
                                    viewModel.completeAssessment(onAssessmentComplete)
                                }
                            )
                        } else {
                            AssessmentMessageBubble(
                                message = message,
                                onQuickReplyClick = { reply ->
                                    viewModel.handleUserAnswer(reply)
                                }
                            )
                        }
                    }
                }

                if (isTyping) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            ThinkingIndicator()
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isGeneratingPlan,
            enter = fadeIn(),
            modifier = Modifier.matchParentSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.BG.copy(alpha = 0.9f))
                    .clickable(enabled = false) {}, // Intercept clicks
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = AppColors.Accent)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.auto_str_جاري_إعداد_خطة),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AssessmentHeader() {
    val context = LocalContext.current
    val lang = remember { UserManager(context).getLanguage() }
    val subtitleText = if (lang == "ar") "مساعدك الذكي للمذاكرة" else "Your AI Learning Assistant"

    Surface(
        color = AppColors.BG,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(AppColors.SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_zeno_logo),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(AppColors.Accent)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Zeno",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Orb(modifier = Modifier.size(10.dp))
                }
                Text(
                    text = subtitleText,
                    fontSize = 12.sp,
                    color = AppColors.TextMuted
                )
            }
        }
    }
}

@Composable
private fun AssessmentMessageBubble(
    message: AssessmentChatMessage,
    onQuickReplyClick: (String) -> Unit
) {
    val isUser = message.sender == MessageSender.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AppColors.SurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_zeno_logo),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        colorFilter = ColorFilter.tint(AppColors.Accent)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(
                modifier = Modifier
                    .widthIn(max = 290.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        )
                    )
                    .background(if (isUser) AppColors.Accent else AppColors.Surface)
                    .border(
                        width = 1.dp,
                        color = if (isUser) Color.Transparent else AppColors.UnfocusedBorder,
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = message.text,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = if (isUser) AppColors.AccentInk else AppColors.TextPrimary
                )
            }
        }

        // Quick Reply Chips
        if (message.quickReplies.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            OptInQuickReplies(
                replies = message.quickReplies,
                onReplySelected = onQuickReplyClick
            )
        }
    }
}

@Composable
private fun OptInQuickReplies(
    replies: List<String>,
    onReplySelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 40.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        replies.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { option ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AppColors.Surface)
                            .border(1.dp, AppColors.AccentSoft, RoundedCornerShape(16.dp))
                            .clickable { onReplySelected(option) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.TextPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun LearningProfileCard(
    profile: LearningProfile,
    onStartClicked: () -> Unit
) {
    CardFun(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        backgroundColor = AppColors.Surface,
        borderColor = AppColors.AccentSoft,
        borderWidth = 1.5.dp,
        cornerRadius = 24.dp,
        contentPadding = 20.dp,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AppColors.SurfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AppColors.Gold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(id = R.string.auto_str_Your_Zeno_Profile),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Strengths Section
                ProfileDetailSection(
                    icon = Icons.Default.CheckCircle,
                    iconTint = AppColors.Accent,
                    title = stringResource(id = R.string.auto_str_Strengths_نقاط_القوة),
                    items = if (profile.strengths.isNotEmpty()) profile.strengths else listOf(stringResource(id = R.string.auto_str_القواعد_الأساسية), stringResource(id = R.string.auto_str_سرعة_التفاعل_والاستيعاب))
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Focus Areas Section
                ProfileDetailSection(
                    icon = Icons.Default.Psychology,
                    iconTint = AppColors.Gold,
                    title = stringResource(id = R.string.auto_str_Focus_Areas_نقاط),
                    items = if (profile.focusAreas.isNotEmpty() || profile.difficultSubjects.isNotEmpty()) {
                        (profile.difficultSubjects + profile.focusAreas).distinct()
                    } else {
                        listOf(stringResource(id = R.string.auto_str_حل_الأسئلة_والتمارين))
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Preferred Explanation
                ProfileDetailSection(
                    icon = Icons.Default.School,
                    iconTint = AppColors.Accent,
                    title = stringResource(id = R.string.auto_str_Preferred_Explanation_أسلوب),
                    items = listOf(profile.preferredExplanationStyle.ifEmpty { stringResource(id = R.string.auto_str_خطوة_بخطوة_مع) })
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Start Button
                Button(
                    onClick = onStartClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
                ) {
                    Text(
                        text = stringResource(id = R.string.auto_str_ابدأ_مع_Zeno),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.AccentInk
                    )
                }
            }
        }
    )
}

@Composable
private fun ProfileDetailSection(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    items: List<String>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items.forEach { itemText ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppColors.SurfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = itemText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextMuted
                    )
                }
            }
        }
    }
}
