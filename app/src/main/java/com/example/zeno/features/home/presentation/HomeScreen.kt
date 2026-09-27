package com.example.zeno.features.home.presentation

import com.example.zeno.core.txt

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.R
import com.example.zeno.core.ui.modifiers.bounceClickable

import com.example.zeno.data.AppColors

private val DarkBG: Color @Composable get() = AppColors.BG
private val CardBG: Color @Composable get() = AppColors.CardBG
private val CardBorder: Color @Composable get() = AppColors.CardBorder
private val LimeAccent: Color @Composable get() = AppColors.LimeAccent
private val TextWhite: Color @Composable get() = AppColors.TextWhite
private val TextMuted: Color @Composable get() = AppColors.TextMuted

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartSession: () -> Unit = {},
    onOpenChat: () -> Unit = {},
    onUpgrade: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }
    val homeData = (uiState as? HomeUiState.Success)?.data

    val streakDays = homeData?.streak ?: 0
    val minutesToday = homeData?.minutesToday ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBG)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.home_greeting),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = homeData?.date?.takeIf { it.isNotBlank() } ?: stringResource(R.string.home_subtitle),
            fontSize = 13.sp,
            color = TextMuted
        )

        val todayProgress = homeData?.todayProgress
        val plannedMinutes = todayProgress?.plannedMinutes ?: 0
        val completedMinutes = todayProgress?.completedMinutes ?: minutesToday
        val completedSessions = todayProgress?.completedSessions ?: 0
        val totalSessions = todayProgress?.totalSessions ?: 0
        val progress = if (plannedMinutes > 0) {
            (completedMinutes.toFloat() / plannedMinutes.toFloat()).coerceIn(0f, 1f)
        } else 0f

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(CardBG)
                .border(1.dp, CardBorder, RoundedCornerShape(22.dp))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.home_today_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = if (plannedMinutes > 0) {
                            stringResource(R.string.home_today_progress, completedMinutes, plannedMinutes)
                        } else {
                            stringResource(R.string.home_today_no_plan)
                        },
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = LimeAccent
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(9.dp).clip(CircleShape),
                color = LimeAccent,
                trackColor = Color(0xFF2B2E38)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.home_sessions_done, completedSessions, totalSessions),
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Text(
                    text = stringResource(R.string.home_minutes_today, completedMinutes),
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(LimeAccent)
                    .bounceClickable { onStartSession() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlayArrow, null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.home_start_session),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        homeData?.nextSession?.let { next ->
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardBG)
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_next_session),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LimeAccent
                )
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = next.title?.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.home_next_session_fallback),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${next.subjectId} • ${next.topicId}",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = onStartSession) {
                    Icon(Icons.Default.PlayArrow, null, tint = LimeAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(R.string.home_continue),
                        color = LimeAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        val weakTopics = homeData?.weakTopics.orEmpty()
        if (weakTopics.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.home_weak_topics),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.home_weak_topics_subtitle),
                fontSize = 12.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(10.dp))
            weakTopics.take(3).forEach { topic ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardBG)
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = topic.topicId,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = stringResource(R.string.home_mastery_percent, (topic.mastery * 100).toInt()),
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                    TextButton(onClick = onOpenChat) {
                        Text(
                            text = stringResource(R.string.home_ask_zeno),
                            color = LimeAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.home_quick_actions),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(CardBG)
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                    .clickable { onOpenChat() }
                    .padding(16.dp)
            ) {
                Column {
                    Icon(Icons.Default.ChatBubbleOutline, null, tint = LimeAccent, modifier = Modifier.size(23.dp))
                    Spacer(modifier = Modifier.height(9.dp))
                    Text(
                        text = stringResource(R.string.home_ask_zeno),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = stringResource(R.string.home_ask_zeno_subtitle),
                        fontSize = 11.sp,
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
                    .clickable { onStartSession() }
                    .padding(16.dp)
            ) {
                Column {
                    Icon(Icons.Default.AutoAwesome, null, tint = LimeAccent, modifier = Modifier.size(23.dp))
                    Spacer(modifier = Modifier.height(9.dp))
                    Text(
                        text = stringResource(R.string.home_practice),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = stringResource(R.string.home_practice_subtitle),
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

    
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBG)
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🔥", fontSize = 22.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "${streakDays} ${stringResource(R.string.home_streak)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = if (completedMinutes > 0) stringResource(R.string.home_streak_goal_reached)
                    else stringResource(R.string.home_streak_start),
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        if (homeData?.isLimitReached == true) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(LimeAccent)
                    .bounceClickable { onUpgrade() }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.home_upgrade_pro_title),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = stringResource(R.string.home_upgrade_pro_subtitle),
                            fontSize = 12.sp,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                    }
                    Text(text = "→", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
