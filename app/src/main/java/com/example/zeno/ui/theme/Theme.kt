package com.example.zeno.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.zeno.data.local.UserManager

data class ZenoColors(
    val BG: Color,
    val Surface: Color,
    val SurfaceVariant: Color,
    val SurfaceVariant2: Color,
    val TextPrimary: Color,
    val TextMuted: Color,
    val TextFaint: Color,
    val UnfocusedBorder: Color,
    val FocusedBorder: Color,
    val Accent: Color,
    val AccentInk: Color,
    val AccentSoft: Color,
    val Gold: Color,
    val GoldSoft: Color,
    val Danger: Color,
    val DangerSoft: Color,
    val UserBubble: Color,
    val UserBubbleText: Color,
    val Black: Color = Color(0x00000000)
)

val LightColors = ZenoColors(
    BG = AppColorsLight.BG,
    Surface = AppColorsLight.Surface,
    SurfaceVariant = AppColorsLight.SurfaceVariant,
    SurfaceVariant2 = AppColorsLight.SurfaceVariant2,
    TextPrimary = AppColorsLight.TextPrimary,
    TextMuted = AppColorsLight.TextMuted,
    TextFaint = AppColorsLight.TextFaint,
    UnfocusedBorder = AppColorsLight.UnfocusedBorder,
    FocusedBorder = AppColorsLight.FocusedBorder,
    Accent = AppColorsLight.Accent,
    AccentInk = AppColorsLight.AccentInk,
    AccentSoft = AppColorsLight.AccentSoft,
    Gold = AppColorsLight.Gold,
    GoldSoft = AppColorsLight.GoldSoft,
    Danger = AppColorsLight.Danger,
    DangerSoft = AppColorsLight.DangerSoft,
    UserBubble = AppColorsLight.UserBubble,
    UserBubbleText = AppColorsLight.UserBubbleText,
    Black = AppColorsLight.Black
)

val DarkColors = ZenoColors(
    BG = AppColorsDark.BG,
    Surface = AppColorsDark.Surface,
    SurfaceVariant = AppColorsDark.SurfaceVariant,
    SurfaceVariant2 = AppColorsDark.SurfaceVariant2,
    TextPrimary = AppColorsDark.TextPrimary,
    TextMuted = AppColorsDark.TextMuted,
    TextFaint = AppColorsDark.TextFaint,
    UnfocusedBorder = AppColorsDark.UnfocusedBorder,
    FocusedBorder = AppColorsDark.FocusedBorder,
    Accent = AppColorsDark.Accent,
    AccentInk = AppColorsDark.AccentInk,
    AccentSoft = AppColorsDark.AccentSoft,
    Gold = AppColorsDark.Gold,
    GoldSoft = AppColorsDark.GoldSoft,
    Danger = AppColorsDark.Danger,
    DangerSoft = AppColorsDark.DangerSoft,
    UserBubble = AppColorsDark.UserBubble,
    UserBubbleText = AppColorsDark.UserBubbleText,
    Black = AppColorsDark.Black
)

val LocalZenoColors = staticCompositionLocalOf { LightColors }

@Composable
fun ZenoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val colors = if (darkTheme) DarkColors else LightColors
    val layoutDirection = if (userManager.getLanguage() == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(
        LocalZenoColors provides colors,
        LocalLayoutDirection provides layoutDirection
    ) {
        MaterialTheme(
            typography = Typography,
            content = content
        )
    }
}