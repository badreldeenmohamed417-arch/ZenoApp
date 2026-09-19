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
    onUpgrade: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }
    val homeData = (uiState as? HomeUiState.Success)?.data

    val streakDays = homeData?.streak ?: 0
    val questionsAsked = homeData?.questionsAskedToday ?: 0
    val minutesToday = homeData?.minutesToday ?: 0
    val weeklyData = homeData?.weeklyStudyTime ?: mapOf(
        stringResource(id = R.string.auto_str_س) to 0, stringResource(id = R.string.auto_str_ح) to 0, stringResource(id = R.string.auto_str_ن) to 0, stringResource(id = R.string.auto_str_ث) to 0, stringResource(id = R.string.auto_str_ر) to 0, stringResource(id = R.string.auto_str_خ) to 0, stringResource(id = R.string.auto_str_ج) to 0
    )
    val subjects = homeData?.subjectMastery ?: emptyList()
    val todayPlan = homeData?.todayPlan ?: emptyList()

    var completedPlanIds by remember(todayPlan) {
        mutableStateOf(todayPlan.filter { it.isCompleted }.map { it.id }.toSet())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBG)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // Top Header
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val rawGreeting = homeData?.greeting?.trim()
                val greetingText = stringResource(R.string.home_greeting)
                Text(
                    text = greetingText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "👋", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.home_subtitle),
                fontSize = 13.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Streak Gauge Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardBG)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dailyGoalStr2 = homeData?.dailyGoal?.replace(Regex("[^0-9]"), "") ?: ""
                val dailyGoalMin2 = dailyGoalStr2.toIntOrNull() ?: 60
                
                val streakText = if (minutesToday == 0) "ابدأ أول جلسة مذاكرة اليوم!"
                                 else if (minutesToday < dailyGoalMin2) "استمر كده متوقفش!"
                                 else "عاش! لقد حققت هدف اليوم 🌟"
                                 
                val emoji = if (minutesToday == 0) "💤" 
                            else if (minutesToday < dailyGoalMin2 / 2) "🔥" 
                            else if (minutesToday < dailyGoalMin2) "🚀" 
                            else "👑"

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$streakDays ${stringResource(R.string.home_streak)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = streakText,
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }

                val activeLime = AppColors.Accent
                // Arc Progress Ring
                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = Color(0xFF2C2F38),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 12f)
                        )
                        val sweep = if (minutesToday == 0) 0f else (minutesToday.toFloat() / dailyGoalMin2.toFloat() * 360f).coerceAtMost(360f)
                        drawArc(
                            color = activeLime,
                            startAngle = -90f,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = 12f, cap = StrokeCap.Round)
                        )
                    }
                    Text(text = emoji, fontSize = 22.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stats 1
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
                        text = "$minutesToday",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.focusMinutes),
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            // Stats 2
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
                        text = "$questionsAsked",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.questionsToZeno),
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Weekly Study Time Chart Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardBG)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.home_weekly_activity),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = stringResource(R.string.home_daily_progress),
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bar Chart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {

                val daysOrder = listOf(stringResource(id = R.string.auto_str_س), stringResource(id = R.string.auto_str_ح), stringResource(id = R.string.auto_str_ن), stringResource(id = R.string.auto_str_ث), stringResource(id = R.string.auto_str_ر), stringResource(id = R.string.auto_str_خ), stringResource(id = R.string.auto_str_ج))
                val maxVal = (weeklyData.values.maxOrNull() ?: 50).coerceAtLeast(1)
                
                // Extract daily goal in minutes
                val dailyGoalStr = homeData?.dailyGoal?.replace(Regex("[^0-9]"), "") ?: ""
                val dailyGoalMin = dailyGoalStr.toIntOrNull() ?: 60

                // Get current day index
                val calendar = java.util.Calendar.getInstance()
                val currentDayStr = when (calendar.get(java.util.Calendar.DAY_OF_WEEK)) {
                    java.util.Calendar.SATURDAY -> stringResource(id = R.string.auto_str_س)
                    java.util.Calendar.SUNDAY -> stringResource(id = R.string.auto_str_ح)
                    java.util.Calendar.MONDAY -> stringResource(id = R.string.auto_str_ن)
                    java.util.Calendar.TUESDAY -> stringResource(id = R.string.auto_str_ث)
                    java.util.Calendar.WEDNESDAY -> stringResource(id = R.string.auto_str_ر)
                    java.util.Calendar.THURSDAY -> stringResource(id = R.string.auto_str_خ)
                    java.util.Calendar.FRIDAY -> stringResource(id = R.string.auto_str_ج)
                    else -> ""
                }

                daysOrder.forEach { day ->
                    val value = weeklyData[day] ?: 0
                    val heightRatio = if (maxVal > 0) (value.toFloat() / maxVal.toFloat()).coerceIn(0.15f, 1f) else 0.15f
                    val isToday = day == currentDayStr
                    
                    val barColor = if (isToday) {
                        if (value == 0) Color.Green
                        else if (value < dailyGoalMin / 3) Color.Red
                        else if (value < dailyGoalMin * 0.8f) Color.Yellow
                        else if (value <= dailyGoalMin * 1.2f) Color.Blue
                        else Color(0xFFFFD700) // Golden
                    } else {
                        Color(0xFF2B2E38) // Other days are not colored as per requirement: "العمود الملون هو العمود تبع اليوم الحالي بس"
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Box(contentAlignment = Alignment.TopCenter) {
                            Box(
                                modifier = Modifier
                                    .padding(top = if (isToday && value > dailyGoalMin * 1.2f) 10.dp else 0.dp)
                                    .width(28.dp)
                                    .fillMaxHeight(heightRatio)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(barColor)
                            )
                            if (isToday && value > dailyGoalMin * 1.2f) {
                                Text(text = "⭐", fontSize = 12.sp, modifier = Modifier.offset(y = (-10).dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = day,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Subject Mastery Level Section
        if (subjects.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardBG)
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.home_subject_mastery),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = stringResource(R.string.ok_button),
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val colors = listOf(
                    LimeAccent,
                    Color(0xFF5CE29A),
                    Color(0xFF60A5FA),
                    Color(0xFFFB923C)
                )

                subjects.forEachIndexed { index, item ->
                    val barColor = colors[index % colors.size]
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${item.percentage}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(barColor)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { item.percentage.toFloat() / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = barColor,
                            trackColor = Color(0xFF2B2E38)
                        )
                    }
                    if (index < subjects.size - 1) {
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Today's Plan Checklist Section
        if (todayPlan.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.home_daily_checklist),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = stringResource(R.string.chat_menu_edit),
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.clickable { onStartSession() }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                todayPlan.forEach { planItem ->
                    val isChecked = completedPlanIds.contains(planItem.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardBG)
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                completedPlanIds = if (isChecked) {
                                    completedPlanIds - planItem.id
                                } else {
                                    completedPlanIds + planItem.id
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isChecked) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = LimeAccent,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.Circle,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = planItem.subject,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isChecked) TextMuted else TextWhite
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = planItem.time,
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Primary CTA Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(LimeAccent)
                .bounceClickable { onStartSession() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.home_start_session),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Upgrade Banner
        if (homeData?.isLimitReached == true) {
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )

                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.home_upgrade_pro_title),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.home_upgrade_pro_subtitle),
                            fontSize = 12.sp,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}
