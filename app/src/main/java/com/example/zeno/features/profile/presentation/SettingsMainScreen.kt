package com.example.zeno.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.zeno.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.zeno.data.AppColors

private val DarkBG: Color @Composable get() = AppColors.BG
private val CardBG: Color @Composable get() = AppColors.CardBG
private val CardBorder: Color @Composable get() = AppColors.CardBorder
private val TextWhite: Color @Composable get() = AppColors.TextWhite
private val TextMuted: Color @Composable get() = AppColors.TextMuted
private val LimeAccent: Color @Composable get() = AppColors.LimeAccent

sealed class SettingsRoute(val route: String) {
    object Main : SettingsRoute("settings_main")
    object Profile : SettingsRoute("settings_profile")
    object Account : SettingsRoute("settings_account")
    object Info : SettingsRoute("settings_info")
    object StudyPlan : SettingsRoute("settings_study_plan")
    object Appearance : SettingsRoute("settings_appearance")
    object Language : SettingsRoute("settings_language")
    object Notifications : SettingsRoute("settings_notifications")
}

@Composable
fun SettingsMainScreen(
    viewModel: ProfileViewModel,
    sessionsViewModel: com.example.zeno.features.session.presentation.SessionsViewModel,
    configRepository: com.example.zeno.core.config.data.repository.ConfigRepository,
    onNavigateToUpgrade: () -> Unit,
    onSubRouteChanged: (Boolean) -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentSubRoute = navBackStackEntry?.destination?.route

    val isSubRoute = currentSubRoute != null && currentSubRoute != SettingsRoute.Main.route

    LaunchedEffect(isSubRoute) {
        onSubRouteChanged(isSubRoute)
    }

    NavHost(navController = navController, startDestination = SettingsRoute.Main.route) {
        composable(SettingsRoute.Main.route) {
            SettingsDashboardScreen(navController, viewModel, onNavigateToUpgrade)
        }
        composable(SettingsRoute.Profile.route) {
            ProfileScreen(navController, viewModel, configRepository)
        }
        composable(SettingsRoute.Account.route) {
            AccountDataScreen(navController, viewModel)
        }
        composable(SettingsRoute.Info.route) {
            AppInfoScreen(navController, configRepository)
        }

        composable(SettingsRoute.Appearance.route) {
            AppearanceScreen(navController, viewModel)
        }
        composable(SettingsRoute.Language.route) {
            LanguageScreen(navController, viewModel)
        }
        composable(SettingsRoute.Notifications.route) {
            NotificationsScreen(navController, viewModel)
        }
    }
}

@Composable
fun SettingsDashboardScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    onNavigateToUpgrade: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()

    val profileData = (uiState as? ProfileUiState.Success)?.data
    val userName = profileData?.displayName ?: stringResource(id = R.string.auto_str_أحمد_سامي)
    val userEmail = profileData?.email ?: "ahmed.sami@example.com"
    val avatarInitial = userName.trim().firstOrNull()?.uppercase() ?: "A"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBG)
    ) {
        // Title Header Surface
        Surface(
            color = Color.Transparent,
            shadowElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = stringResource(id = R.string.settings_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // User Avatar Profile Card
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(com.example.zeno.data.AppColors.SurfaceVariant)
                    .border(2.dp, com.example.zeno.data.AppColors.CardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarInitial,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = com.example.zeno.data.AppColors.TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = userName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = com.example.zeno.data.AppColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = userEmail,
                fontSize = 13.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Group 1: Profile & Subscription
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardBG)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            SettingsRowItem(
                title = stringResource(id = R.string.nav_profile),
                icon = Icons.Default.Person,
                onClick = { navController.navigate(SettingsRoute.Profile.route) }
            )

            HorizontalDivider(color = CardBorder, thickness = 1.dp)
            SettingsRowItem(
                title = stringResource(id = R.string.nav_upgrade),
                badge = stringResource(id = R.string.premium_column_free),
                icon = Icons.Default.EmojiEvents,
                onClick = { onNavigateToUpgrade() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Group 2: Preferences
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardBG)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            SettingsRowItem(
                title = stringResource(id = R.string.settings_appearance),
                icon = Icons.Default.BrightnessMedium,
                onClick = { navController.navigate(SettingsRoute.Appearance.route) }
            )
            HorizontalDivider(color = CardBorder, thickness = 1.dp)
            SettingsRowItem(
                title = stringResource(id = R.string.settings_language),
                icon = Icons.Default.Language,
                onClick = { navController.navigate(SettingsRoute.Language.route) }
            )
            HorizontalDivider(color = CardBorder, thickness = 1.dp)
            SettingsRowItem(
                title = stringResource(id = R.string.settings_notifications),
                icon = Icons.Default.Notifications,
                onClick = { navController.navigate(SettingsRoute.Notifications.route) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Group 3: Account & Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardBG)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            SettingsRowItem(
                title = stringResource(id = R.string.settings_account_data),
                icon = Icons.Default.ManageAccounts,
                onClick = { navController.navigate(SettingsRoute.Account.route) }
            )
            HorizontalDivider(color = CardBorder, thickness = 1.dp)
            SettingsRowItem(
                title = stringResource(id = R.string.settings_about_app),
                icon = Icons.Default.Info,
                onClick = { navController.navigate(SettingsRoute.Info.route) }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
}

@Composable
fun SettingsRowItem(
    title: String,
    icon: ImageVector,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(com.example.zeno.data.AppColors.SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = com.example.zeno.data.AppColors.Accent,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badge != null) {
                Text(
                    text = badge,
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
