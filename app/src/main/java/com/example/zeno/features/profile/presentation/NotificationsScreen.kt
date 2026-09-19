package com.example.zeno.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.zeno.R
import com.example.zeno.core.widgets.SettingsTopHeaderBar
import com.example.zeno.data.AppColors
import com.example.zeno.features.student.data.dto.UpdateNotificationPreferencesRequest

private val DarkBG: Color @Composable get() = AppColors.BG
private val CardBG: Color @Composable get() = AppColors.CardBG
private val CardBorder: Color @Composable get() = AppColors.CardBorder
private val TextWhite: Color @Composable get() = AppColors.TextWhite
private val TextMuted: Color @Composable get() = AppColors.TextMuted
private val LimeAccent: Color @Composable get() = AppColors.LimeAccent

@Composable
fun NotificationsScreen(navController: NavController, viewModel: ProfileViewModel) {
    val notifs by viewModel.notificationsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBG)
    ) {
        SettingsTopHeaderBar(
            title = stringResource(id = R.string.settings_notifications),
            onBackClick = { navController.popBackStack() }
        )

        Column(modifier = Modifier.padding(24.dp)) {
            val studyReminders = notifs?.studyReminders ?: true
            val planUpdates = notifs?.planUpdates ?: true
            val dailySummary = notifs?.dailySummary ?: true
            val quietHoursEnabled = notifs?.quietHoursEnabled ?: false

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardBG)
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                NotificationToggleItem(
                    title = stringResource(id = R.string.notif_study_reminders_title),
                    description = stringResource(id = R.string.notif_study_reminders_desc),
                    isChecked = studyReminders,
                    onCheckedChange = {
                        viewModel.updateNotifications(
                            UpdateNotificationPreferencesRequest(
                                timezone = notifs?.timezone ?: "UTC",
                                quietHoursEnabled = quietHoursEnabled,
                                quietHoursStart = notifs?.quietHoursStart,
                                quietHoursEnd = notifs?.quietHoursEnd,
                                studyReminders = it,
                                planUpdates = planUpdates,
                                dailySummary = dailySummary
                            )
                        )
                    }
                )

                HorizontalDivider(color = CardBorder, thickness = 1.dp)

                NotificationToggleItem(
                    title = stringResource(id = R.string.notif_plan_updates_title),
                    description = stringResource(id = R.string.notif_plan_updates_desc),
                    isChecked = planUpdates,
                    onCheckedChange = {
                        viewModel.updateNotifications(
                            UpdateNotificationPreferencesRequest(
                                timezone = notifs?.timezone ?: "UTC",
                                quietHoursEnabled = quietHoursEnabled,
                                quietHoursStart = notifs?.quietHoursStart,
                                quietHoursEnd = notifs?.quietHoursEnd,
                                studyReminders = studyReminders,
                                planUpdates = it,
                                dailySummary = dailySummary
                            )
                        )
                    }
                )

                HorizontalDivider(color = CardBorder, thickness = 1.dp)

                NotificationToggleItem(
                    title = stringResource(id = R.string.notif_daily_summary_title),
                    description = stringResource(id = R.string.notif_daily_summary_desc),
                    isChecked = dailySummary,
                    onCheckedChange = {
                        viewModel.updateNotifications(
                            UpdateNotificationPreferencesRequest(
                                timezone = notifs?.timezone ?: "UTC",
                                quietHoursEnabled = quietHoursEnabled,
                                quietHoursStart = notifs?.quietHoursStart,
                                quietHoursEnd = notifs?.quietHoursEnd,
                                studyReminders = studyReminders,
                                planUpdates = planUpdates,
                                dailySummary = it
                            )
                        )
                    }
                )

                HorizontalDivider(color = CardBorder, thickness = 1.dp)

                NotificationToggleItem(
                    title = stringResource(id = R.string.notif_quiet_hours_title),
                    description = stringResource(id = R.string.notif_quiet_hours_desc),
                    isChecked = quietHoursEnabled,
                    onCheckedChange = {
                        viewModel.updateNotifications(
                            UpdateNotificationPreferencesRequest(
                                timezone = notifs?.timezone ?: "UTC",
                                quietHoursEnabled = it,
                                quietHoursStart = notifs?.quietHoursStart,
                                quietHoursEnd = notifs?.quietHoursEnd,
                                studyReminders = studyReminders,
                                planUpdates = planUpdates,
                                dailySummary = dailySummary
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun NotificationToggleItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = TextMuted,
                lineHeight = 16.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = LimeAccent,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = Color(0xFF262930)
            )
        )
    }
}
