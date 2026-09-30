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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.R
import com.example.zeno.core.ui.modifiers.bounceClickable

import com.example.zeno.data.AppColors
import java.util.Calendar

private val DarkBG: Color @Composable get() = AppColors.BG
private val CardBG: Color @Composable get() = AppColors.CardBG
private val CardBorder: Color @Composable get() = AppColors.CardBorder
private val LimeAccent: Color @Composable get() = AppColors.LimeAccent
private val TextWhite: Color @Composable get() = AppColors.TextWhite
private val TextMuted: Color @Composable get() = AppColors.TextMuted

private fun getWeeklyMinutesForDay(dayIndex: Int, weeklyData: Map<String, Int>?): Int {
    if (weeklyData.isNullOrEmpty()) return 0

    val satKeys = listOf("sat", "saturday", "س", "سبت", "السبت", "6")
    val sunKeys = listOf("sun", "sunday", "ح", "أحد", "الأحد", "0", "7")
    val monKeys = listOf("mon", "monday", "ن", "إثنين", "الإثنين", "1")
    val tueKeys = listOf("tue", "tuesday", "ث", "ثلاثاء", "الثلاثاء", "2")
    val wedKeys = listOf("wed", "wednesday", "ر", "أربعاء", "الأربعاء", "3")
    val thuKeys = listOf("thu", "thursday", "خ", "خميس", "الخميس", "4")
    val friKeys = listOf("fri", "friday", "ج", "جمعة", "الجمعة", "5")

    val matchingKeys = when (dayIndex) {
        0 -> satKeys
        1 -> sunKeys
        2 -> monKeys
        3 -> tueKeys
        4 -> wedKeys
        5 -> thuKeys
        6 -> friKeys
        else -> emptyList()
    }

    for ((key, value) in weeklyData) {
        val lowerKey = key.trim().lowercase()
        if (matchingKeys.any { lowerKey == it || lowerKey.contains(it) }) {
            return value
        }
    }

    return 0
}

private fun formatMinutesDisplay(minutes: Int): String {
    return if (minutes >= 60) {
        val hrs = minutes / 60
        val mins = minutes % 60
        if (mins > 0) "${hrs}س ${mins}د" else "${hrs}س"
    } else {
        "$minutes د"
    }
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartSession: () -> Unit = {},
    onUpgrade: () -> Unit = {},
    onNavigateToStudio: () -> Unit = {}
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
                
                val streakText = if (minutesToday == 0) stringResource(R.string.home_streak_start)
                                 else if (minutesToday < dailyGoalMin2) stringResource(R.string.home_streak_keep_going)
                                 else stringResource(R.string.home_streak_goal_reached)
                                 
                val progressPercent = if (dailyGoalMin2 > 0) ((minutesToday.toFloat() / dailyGoalMin2.toFloat()) * 100).toInt().coerceAtMost(100) else 0

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
                    Text(
                        text = "$progressPercent%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stats 1 - Focus Minutes
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(CardBG)
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = formatMinutesDisplay(minutesToday),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.focusMinutes),
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(LimeAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = LimeAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Stats 2 - Questions Asked
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(CardBG)
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$questionsAsked",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.questionsToZeno),
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF60A5FA).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null,
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Studio Learning Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            CardBG,
                            LimeAccent.copy(alpha = 0.15f)
                        )
                    )
                )
                .border(1.dp, LimeAccent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .bounceClickable { onNavigateToStudio() }
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.studio_title),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = LimeAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.studio_home_subtitle),
                        fontSize = 12.5.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LimeAccent)
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.studio_home_btn),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
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
            val totalWeeklyMins = (0..6).sumOf { getWeeklyMinutesForDay(it, weeklyData) }
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
                    text = "إجمالي: ${formatMinutesDisplay(totalWeeklyMins)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LimeAccent
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bar Chart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val daysLabels = listOf("س", "ح", "ن", "ث", "ر", "خ", "ج")
                val dayValues = (0..6).map { getWeeklyMinutesForDay(it, weeklyData) }
                val maxVal = dayValues.maxOrNull()?.coerceAtLeast(30) ?: 60

                val calendar = Calendar.getInstance()
                val currentDayIndex = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.SATURDAY -> 0
                    Calendar.SUNDAY -> 1
                    Calendar.MONDAY -> 2
                    Calendar.TUESDAY -> 3
                    Calendar.WEDNESDAY -> 4
                    Calendar.THURSDAY -> 5
                    Calendar.FRIDAY -> 6
                    else -> 0
                }

                daysLabels.forEachIndexed { i, day ->
                    val value = dayValues[i]
                    val isToday = i == currentDayIndex
                    val heightRatio = if (value > 0) (value.toFloat() / maxVal.toFloat()).coerceIn(0.18f, 1f) else 0.08f

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            text = if (value > 0) "${value}د" else "-",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (value > 0) TextWhite else TextMuted.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .fillMaxHeight(heightRatio)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (value > 0) {
                                        Brush.verticalGradient(
                                            listOf(LimeAccent, LimeAccent.copy(alpha = 0.65f))
                                        )
                                    } else {
                                        SolidColor(Color(0xFF2C2F38))
                                    }
                                )
                                .then(
                                    if (isToday) Modifier.border(1.dp, LimeAccent, RoundedCornerShape(8.dp)) else Modifier
                                )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = day,
                            fontSize = 12.sp,
                            fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Bold,
                            color = if (isToday) LimeAccent else TextMuted
                        )
                        if (isToday) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(LimeAccent)
                            )
                        }
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
