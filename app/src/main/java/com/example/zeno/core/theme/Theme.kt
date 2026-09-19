package com.example.zeno.core.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat
import com.example.zeno.data.local.UserManager
import com.example.zeno.ui.theme.DarkColors
import com.example.zeno.ui.theme.LightColors
import com.example.zeno.ui.theme.LocalZenoColors

private val DarkColorScheme = darkColorScheme(
    primary = Lime500,
    onPrimary = Slate950,
    primaryContainer = Lime800,
    onPrimaryContainer = Lime100,
    
    secondary = Lime300,
    onSecondary = Slate900,
    secondaryContainer = Lime900,
    onSecondaryContainer = Lime200,

    background = Slate950,
    onBackground = Slate50,
    surface = Slate900,
    onSurface = Slate50,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate300,
    
    error = ErrorRed,
    onError = Slate50,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
    
    outline = Slate600
)

private val LightColorScheme = lightColorScheme(
    primary = Lime600,
    onPrimary = Slate50,
    primaryContainer = Lime100,
    onPrimaryContainer = Lime900,
    
    secondary = Lime500,
    onSecondary = Slate50,
    secondaryContainer = Lime50,
    onSecondaryContainer = Lime800,
    
    background = Slate50,
    onBackground = Slate900,
    surface = Color(0xFFFFFFFF),
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    
    error = ErrorRed,
    onError = Slate50,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF991B1B),
    
    outline = Slate300
)

@Composable
fun ZenoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val isDark = userManager.getThemeMode(darkTheme)

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    val zenoColors = if (isDark) DarkColors else LightColors
    val layoutDirection = if (userManager.getLanguage() == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = zenoColors.BG.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    CompositionLocalProvider(
        LocalZenoColors provides zenoColors,
        LocalLayoutDirection provides layoutDirection
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ZenoTypography,
            content = content
        )
    }
}
