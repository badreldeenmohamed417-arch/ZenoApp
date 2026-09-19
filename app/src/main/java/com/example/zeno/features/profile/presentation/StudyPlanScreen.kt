package com.example.zeno.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.zeno.R
import com.example.zeno.core.widgets.SettingsTopHeaderBar
import com.example.zeno.data.AppColors

import com.example.zeno.features.session.presentation.SessionsViewModel
import com.example.zeno.features.session.presentation.SessionsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlanScreen(navController: NavController, sessionsViewModel: SessionsViewModel) {
    val uiState by sessionsViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            SettingsTopHeaderBar(
                title = stringResource(id = R.string.settings_study_plan),
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = AppColors.BG,
        floatingActionButton = {
            if (uiState is SessionsUiState.Success) {
                FloatingActionButton(
                    onClick = { sessionsViewModel.generateStudyPlan(180) },
                    containerColor = AppColors.Accent,
                    contentColor = AppColors.AccentInk
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is SessionsUiState.Loading -> {
                    CircularProgressIndicator(color = AppColors.Accent)
                }
                is SessionsUiState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = AppColors.TextFaint,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.auto_str_لا_توجد_خطة),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(id = R.string.auto_str_يبدو_أنك_لم),
                            fontSize = 14.sp,
                            color = AppColors.TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { sessionsViewModel.generateStudyPlan(180) },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                stringResource(id = R.string.auto_str_إنشاء_خطة_جديدة),
                                color = AppColors.AccentInk,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                is SessionsUiState.Success -> {
                    val plan = (uiState as SessionsUiState.Success).plan
                    val planItemsAll = plan.items
                    
                    // Filter to show only the current week's items
                    val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                    var daysSinceStart = 0L
                    try {
                        val startDate = dateFormat.parse(plan.startDate) ?: java.util.Date()
                        val now = java.util.Date()
                        val diff = now.time - startDate.time
                        daysSinceStart = maxOf(0L, diff / (1000 * 60 * 60 * 24))
                    } catch (e: Exception) { e.printStackTrace() }
                    
                    val currentWeekIndex = (daysSinceStart / 7).toInt()
                    val startIndex = currentWeekIndex * 7 * 3 // 3 items per day
                    val endIndex = startIndex + (7 * 3)
                    
                    val planItems = if (planItemsAll.size > startIndex) {
                        planItemsAll.subList(startIndex, minOf(endIndex, planItemsAll.size))
                    } else planItemsAll

                    if (planItems.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.auto_str_الخطة_فارغة),
                            fontSize = 16.sp,
                            color = AppColors.TextMuted
                        )
                    } else {
                        val planDays = planItems.chunked(3).mapIndexed { index, items ->
                            val dayName = when (index) {
                                0 -> stringResource(id = R.string.auto_str_اليوم_الأول)
                                1 -> stringResource(id = R.string.auto_str_اليوم_الثاني)
                                2 -> stringResource(id = R.string.auto_str_اليوم_الثالث)
                                3 -> stringResource(id = R.string.auto_str_اليوم_الرابع)
                                4 -> stringResource(id = R.string.auto_str_اليوم_الخامس)
                                5 -> stringResource(id = R.string.auto_str_اليوم_السادس)
                                6 -> stringResource(id = R.string.auto_str_اليوم_السابع)
                                else -> stringResource(id = R.string.auto_str_يوم_index_1)
                            }
                            Pair(dayName, items.map { stringResource(id = R.string.auto_str_itsubjectId_ittopicId_itplannedDurationMinutes, it.subjectId, it.topicId, it.plannedDurationMinutes) })
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    text = stringResource(id = R.string.auto_str_خطتك_الأسبوعية),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.TextPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(id = R.string.auto_str_تم_بناء_هذه),
                                    fontSize = 13.sp,
                                    color = AppColors.TextMuted
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            items(planDays) { day ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(AppColors.Surface)
                                        .border(1.dp, AppColors.UnfocusedBorder, RoundedCornerShape(16.dp))
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = day.first,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    if (day.second.isEmpty()) {
                                        Text(
                                            text = stringResource(id = R.string.auto_str_يوم_راحة),
                                            fontSize = 14.sp,
                                            color = AppColors.TextFaint
                                        )
                                    } else {
                                        day.second.forEach { sessionStr ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(AppColors.Accent)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = sessionStr,
                                                    fontSize = 14.sp,
                                                    color = AppColors.TextPrimary
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(80.dp)) // Extra space for FAB
                            }
                        }
                    }
                }
            }
        }
    }
}

