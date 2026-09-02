package com.example.zeno.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.zeno.R
import com.example.zeno.core.ui.modifiers.bounceClickable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class SettingsRoute(val route: String) {
    object Main : SettingsRoute("settings_main")
    object Profile : SettingsRoute("settings_profile")
    object Account : SettingsRoute("settings_account")
    object Info : SettingsRoute("settings_info")
}

@Composable
fun SettingsMainScreen(viewModel: ProfileViewModel) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = SettingsRoute.Main.route) {
        composable(SettingsRoute.Main.route) {
            SettingsDashboardScreen(navController)
        }
        composable(SettingsRoute.Profile.route) {
            ProfileScreen(navController, viewModel)
        }
        composable(SettingsRoute.Account.route) {
            AccountDataScreen(navController)
        }
        composable(SettingsRoute.Info.route) {
            AppInfoScreen(navController)
        }
    }
}

@Composable
fun SettingsDashboardScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.settings_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(24.dp)
            )
        }

        Column(modifier = Modifier.padding(24.dp)) {
            SettingsItem(
                icon = Icons.Default.Person,
                title = stringResource(id = R.string.settings_profile),
                onClick = { navController.navigate(SettingsRoute.Profile.route) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SettingsItem(
                icon = Icons.Default.Lock,
                title = stringResource(id = R.string.settings_account),
                onClick = { navController.navigate(SettingsRoute.Account.route) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SettingsItem(
                icon = Icons.Default.Info,
                title = stringResource(id = R.string.settings_info),
                onClick = { navController.navigate(SettingsRoute.Info.route) }
            )
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
