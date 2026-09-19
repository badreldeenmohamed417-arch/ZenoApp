package com.example.zeno.features.profile.presentation

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.zeno.R
import com.example.zeno.core.widgets.SettingsTopHeaderBar
import com.example.zeno.data.AppColors
import com.example.zeno.data.local.UserManager

private val DarkBG: Color @Composable get() = AppColors.BG
private val CardBG: Color @Composable get() = AppColors.CardBG
private val CardBorder: Color @Composable get() = AppColors.CardBorder
private val TextWhite: Color @Composable get() = AppColors.TextWhite
private val TextMuted: Color @Composable get() = AppColors.TextMuted
private val LimeAccent: Color @Composable get() = AppColors.LimeAccent

@Composable
fun AppearanceScreen(navController: NavController, viewModel: ProfileViewModel) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    var selectedTheme by remember { mutableStateOf(userManager.getThemeModeString()) }

    fun selectTheme(mode: String) {
        if (selectedTheme != mode) {
            selectedTheme = mode
            userManager.saveThemeModeString(mode)
            viewModel.updateSettings(theme = mode)
            (context as? Activity)?.recreate()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBG)
    ) {
        SettingsTopHeaderBar(
            title = stringResource(id = R.string.settings_appearance),
            onBackClick = { navController.popBackStack() }
        )

        Column(modifier = Modifier.padding(24.dp)) {
            ThemeOptionItem(
                title = stringResource(id = R.string.auto_str_فاتح_Light),
                isSelected = selectedTheme == "light",
                onClick = { selectTheme("light") }
            )
            Spacer(modifier = Modifier.height(12.dp))
            ThemeOptionItem(
                title = stringResource(id = R.string.auto_str_داكن_Dark),
                isSelected = selectedTheme == "dark",
                onClick = { selectTheme("dark") }
            )
            Spacer(modifier = Modifier.height(12.dp))
            ThemeOptionItem(
                title = stringResource(id = R.string.auto_str_تلقائي_حسب_النظام),
                isSelected = selectedTheme == "system",
                onClick = { selectTheme("system") }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.auto_str_ملاحظة_سيتم_تطبيق),
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
fun ThemeOptionItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBG)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) LimeAccent else CardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = TextWhite,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = LimeAccent
            )
        }
    }
}
