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
fun LanguageScreen(navController: NavController, viewModel: ProfileViewModel) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    var selectedLanguage by remember { mutableStateOf(userManager.getLanguage()) }

    fun selectLanguage(lang: String) {
        if (selectedLanguage != lang) {
            selectedLanguage = lang
            userManager.saveLanguage(lang)
            viewModel.updateSettings(language = lang)
            (context as? Activity)?.recreate()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBG)
    ) {
        SettingsTopHeaderBar(
            title = stringResource(id = R.string.settings_language),
            onBackClick = { navController.popBackStack() }
        )

        Column(modifier = Modifier.padding(24.dp)) {
            LanguageOptionItem(
                title = stringResource(id = R.string.auto_str_العربية),
                isSelected = selectedLanguage == "ar",
                onClick = { selectLanguage("ar") }
            )
            Spacer(modifier = Modifier.height(12.dp))
            LanguageOptionItem(
                title = "English",
                isSelected = selectedLanguage == "en",
                onClick = { selectLanguage("en") }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.auto_str_ملاحظة_تغيير_لغة),
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
fun LanguageOptionItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
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
