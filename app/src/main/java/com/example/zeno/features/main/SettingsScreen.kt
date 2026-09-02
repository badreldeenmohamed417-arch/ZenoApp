package com.example.zeno.futures.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors
import com.example.zeno.data.local.UserManager
import com.example.zeno.core.widgets.BottomNavItem
import com.example.zeno.core.widgets.ZenoBottomNavigationBar
import com.example.zeno.data.repository.UserRepository
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onHome: () -> Unit,
    onChats: () -> Unit,
    onTermsOfUse: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val userRepository = remember { UserRepository() }
    val scope = rememberCoroutineScope()

    var isDarkMode by remember { mutableStateOf(userManager.getThemeMode(false)) }
    var userEmail by remember { mutableStateOf(userManager.getEmail() ?: "") }
    var userGrade by remember { mutableStateOf(userManager.getGrade() ?: "") }
    var userSection by remember { mutableStateOf(userManager.getSchoolSystem() ?: "") }
    var displayName by remember { mutableStateOf(userManager.getDisplayName() ?: "") }

    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showReportDropdown by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showUserDetailDialog by remember { mutableStateOf<String?>(null) } // "email", "grade", "section"

    LaunchedEffect(Unit) {
        try {
            val user = userRepository.getMe()
            userEmail = user.email
            userGrade = user.grade ?: ""
            userSection = user.schoolSystem ?: ""
            displayName = user.displayName ?: ""
            
            userManager.saveEmail(user.email)
            userManager.saveAcademicData(user.grade, user.schoolSystem)
            userManager.saveProfileData(user.displayName, userManager.getBirthDate(), user.country, user.email)
        } catch (e: Exception) {
            // Fallback to local data already loaded
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BG)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Header()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                SettingsGroup(txt("settingsSectionAccount")) {
                    SettingsRow(txt("emailLabel"), userEmail.ifEmpty { "..." }, onClick = { showUserDetailDialog = "email" })
                    SettingsRow(txt("settingsGrade"), userGrade.ifEmpty { "..." }, onClick = { showUserDetailDialog = "grade" })
                    SettingsRow(txt("settingsSection"), userSection.ifEmpty { "..." }, onClick = { showUserDetailDialog = "section" })
                }

                SettingsGroup(txt("settingsSectionApp")) {
                    SettingsSwitchRow(txt("settingsDarkMode"), isDarkMode) { 
                        isDarkMode = it
                        userManager.saveThemeMode(it)
                        (context as? Activity)?.recreate()
                    }
                    SettingsRow(txt("settingsLanguage"), if (userManager.getLanguage() == "ar") "العربية" else "English") {
                        showLanguageDialog = true
                    }
                }

                SettingsGroup(txt("settingsSectionOther")) {
                    SettingsRow(txt("settingsAbout"), showArrow = true) { showAboutDialog = true }
                    SettingsRow(txt("settingsPrivacyPolicy"), showArrow = true) { showPrivacyDialog = true }
                    SettingsRow(txt("settingsTermsOfUse"), showArrow = true) { onTermsOfUse() }
                    
                    Column {
                        SettingsRow(txt("settingsReportProblem"), showArrow = true) {
                            showReportDropdown = !showReportDropdown
                        }
                        if (showReportDropdown) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .background(AppColors.SurfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = txt("report_problem_hint"),
                                    fontSize = 13.sp,
                                    color = AppColors.TextMuted,
                                    textAlign = TextAlign.Start
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = Uri.parse("mailto:support@nexorai.top")
                                            putExtra(Intent.EXTRA_SUBJECT, "Report a Problem - Zeno")
                                        }
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.align(Alignment.End),
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
                                ) {
                                    Text(txt("send_email_button"), color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.DangerSoft)
                ) {
                    Text(
                        txt("settingsLogout"),
                        color = AppColors.Danger,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = txt("settingsFooter"),
                    modifier = Modifier.padding(vertical = 22.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = AppColors.TextFaint
                )
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
        ) {
            ZenoBottomNavigationBar(
                selectedTab = BottomNavItem.SETTINGS,
                onTabSelected = { item ->
                    when (item) {
                        BottomNavItem.HOME -> onHome()
                        BottomNavItem.CHATS -> onChats()
                        else -> {}
                    }
                }
            )
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = txt("logoutConfirmTitle")) },
            text = { Text(text = txt("logoutConfirmMessage")) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        userManager.clearUserData()
                        onLogout()
                    }
                ) {
                    Text(text = txt("logoutConfirmAction"), color = AppColors.Danger)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(text = txt("cancel"))
                }
            }
        )
    }

    if (showAboutDialog) {
        InfoDialog(
            title = txt("about_zeno_title"),
            content = txt("about_zeno_content"),
            onDismiss = { showAboutDialog = false }
        )
    }

    if (showPrivacyDialog) {
        InfoDialog(
            title = txt("privacy_policy_title"),
            content = txt("privacy_policy_content"),
            onDismiss = { showPrivacyDialog = false }
        )
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = userManager.getLanguage(),
            onLanguageSelected = { lang ->
                userManager.saveLanguage(lang)
                showLanguageDialog = false
                (context as? Activity)?.recreate()
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    showUserDetailDialog?.let { type ->
        val title = when(type) {
            "email" -> txt("emailLabel")
            "grade" -> txt("settingsGrade")
            else -> txt("settingsSection")
        }
        val value = when(type) {
            "email" -> userEmail
            "grade" -> userGrade
            else -> userSection
        }
        UserDetailDialog(title, value) { showUserDetailDialog = null }
    }
}

@Composable
fun InfoDialog(title: String, content: String, onDismiss: () -> Unit) {
    // معالجة النص المؤجل لمنع الـ blocking
    val processedContent = remember(content) { content }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            color = AppColors.Surface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                // استخدام Column مع Scroll متزن ومستقل
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = processedContent,
                        fontSize = 14.sp,
                        color = AppColors.TextMuted,
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
                ) {
                    Text(txt("close_button"), color = Color.White)
                }
            }
        }
    }
}

@Composable
fun LanguageSelectionDialog(currentLanguage: String, onLanguageSelected: (String) -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AppColors.Surface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(txt("settingsLanguage"), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                LanguageOption("العربية", "ar", currentLanguage == "ar") { onLanguageSelected("ar") }
                LanguageOption("English", "en", currentLanguage == "en") { onLanguageSelected("en") }
            }
        }
    }
}

@Composable
fun LanguageOption(label: String, code: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 16.sp, color = if (isSelected) AppColors.Accent else AppColors.TextPrimary)
        if (isSelected) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = AppColors.Accent)
        }
    }
}

@Composable
fun UserDetailDialog(title: String, value: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AppColors.Surface
        ) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, fontSize = 16.sp, color = AppColors.TextMuted)
                Spacer(modifier = Modifier.height(8.dp))
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)) {
                    Text(txt("ok_button"), color = Color.White)
                }
            }
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
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
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
