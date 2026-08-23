package com.example.zeno.futures.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    var isDarkMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BG)
    ) {
        Header()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsGroup(txt("settingsSectionAccount")) {
                SettingsRow(txt("emailLabel"), "ahmed@mail.com")
                SettingsRow(txt("settingsGrade"), "ثانية ثانوي")
                SettingsRow(txt("settingsSection"), "شرعي")
            }

            SettingsGroup(txt("settingsSectionApp")) {
                SettingsSwitchRow(txt("settingsDarkMode"), isDarkMode) { isDarkMode = it }
                SettingsRow(txt("settingsLanguage"), txt("settingsLanguageValue"))
            }

            SettingsGroup(txt("settingsSectionSubscription")) {
                SettingsRow(txt("settingsPremium"), txt("settingsPremiumInactive"), showBadge = true)
                SettingsRow(txt("settingsManageSubscription"), showArrow = true)
            }

            SettingsGroup(txt("settingsSectionOther")) {
                SettingsRow(txt("settingsAbout"), showArrow = true)
                SettingsRow(txt("settingsPrivacyPolicy"), showArrow = true)
                SettingsRow(txt("settingsTermsOfUse"), showArrow = true)
                SettingsRow(txt("settingsReportProblem"), showArrow = true)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.DangerSoft)
            ) {
                Text(txt("settingsLogout"), color = AppColors.Danger, fontWeight = FontWeight.Bold)
            }

            Text(
                text = txt("settingsFooter"),
                modifier = Modifier.padding(vertical = 22.dp).fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 12.sp,
                color = AppColors.TextFaint
            )
        }
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp, 20.dp, 18.dp, 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = txt("settingsTitle"),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextMuted,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 18.dp)
        )
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(AppColors.Surface)
                .border(1.dp, AppColors.UnfocusedBorder, RoundedCornerShape(12.dp))
        ) {
            content()
        }
    }
}

@Composable
fun SettingsRow(
    label: String,
    value: String? = null,
    showArrow: Boolean = false,
    showBadge: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(15.dp, 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value != null) {
                if (showBadge) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AppColors.SurfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AppColors.TextMuted)
                    }
                } else {
                    Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AppColors.TextMuted)
                }
            }
            if (showArrow) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = AppColors.TextFaint,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
    HorizontalDivider(color = AppColors.UnfocusedBorder, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
fun SettingsSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp, 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AppColors.Accent,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = AppColors.SurfaceVariant2,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
    HorizontalDivider(color = AppColors.UnfocusedBorder, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
}
