package com.example.zeno.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.zeno.ui.theme.LocalZenoColors

object AppColors {
    val BG: Color @Composable get() = LocalZenoColors.current.BG
    val Surface: Color @Composable get() = LocalZenoColors.current.Surface
    val SurfaceVariant: Color @Composable get() = LocalZenoColors.current.SurfaceVariant
    val SurfaceVariant2: Color @Composable get() = LocalZenoColors.current.SurfaceVariant2
    val TextPrimary: Color @Composable get() = LocalZenoColors.current.TextPrimary
    val TextMuted: Color @Composable get() = LocalZenoColors.current.TextMuted
    val TextFaint: Color @Composable get() = LocalZenoColors.current.TextFaint
    val UnfocusedBorder: Color @Composable get() = LocalZenoColors.current.UnfocusedBorder
    val FocusedBorder: Color @Composable get() = LocalZenoColors.current.FocusedBorder
    val Accent: Color @Composable get() = LocalZenoColors.current.Accent
    val AccentInk: Color @Composable get() = LocalZenoColors.current.AccentInk
    val AccentSoft: Color @Composable get() = LocalZenoColors.current.AccentSoft
    val Gold: Color @Composable get() = LocalZenoColors.current.Gold
    val GoldSoft: Color @Composable get() = LocalZenoColors.current.GoldSoft
    val Danger: Color @Composable get() = LocalZenoColors.current.Danger
    val DangerSoft: Color @Composable get() = LocalZenoColors.current.DangerSoft
    val UserBubble: Color @Composable get() = LocalZenoColors.current.UserBubble
    val UserBubbleText: Color @Composable get() = LocalZenoColors.current.UserBubbleText
    val Black: Color @Composable get() = LocalZenoColors.current.Black
}