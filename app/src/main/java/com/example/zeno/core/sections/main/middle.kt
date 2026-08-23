package com.example.zeno.core.sections.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.core.theme.CardFun
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors

@Composable
fun HomeScreenContent(
    userName: String = "محمد",
    streakDays: Int = 4,
    onStartSessionClick: () -> Unit = {},
    onAskZenoClick: () -> Unit = {}
) {
    val cardsScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BG)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.End
    ) {
        // --- Header Section (ثابت - لا يتأثر بالسكرول) ---
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = txt("home_welcome_title", userName),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary,
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = txt("home_subtitle"),
            fontSize = 14.sp,
            color = AppColors.TextMuted,
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Streak Badge (ثابت أيضاً) ---
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(AppColors.GoldSoft)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = txt("home_streak_badge", streakDays),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Gold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Cards Container (قابل للتمرير فقط) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(cardsScrollState),
            horizontalAlignment = Alignment.End
        ) {
            // Card 1: Start Session
            ActionCard(
                title = txt("home_card_start_session_title"),
                description = txt("home_card_start_session_desc"),
                icon = Icons.Outlined.Timer,
                isPrimary = true,
                onClick = onStartSessionClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 2: Ask Zeno
            ActionCard(
                title = txt("home_card_ask_zeno_title"),
                description = txt("home_card_ask_zeno_desc"),
                icon = Icons.Outlined.ChatBubbleOutline,
                isPrimary = false,
                onClick = onAskZenoClick
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    CardFun(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        backgroundColor = if (isPrimary) AppColors.AccentSoft else AppColors.Surface,
        borderColor = if (isPrimary) AppColors.Accent else AppColors.UnfocusedBorder,
        borderWidth = 1.dp,
        cornerRadius = 24.dp,
        contentPadding = 20.dp,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                // Icon at Top End
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isPrimary) AppColors.Accent else AppColors.SurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isPrimary) AppColors.AccentInk else AppColors.TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title and Arrow Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = AppColors.TextFaint,
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Description
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = AppColors.TextMuted,
                    textAlign = TextAlign.End,
                    lineHeight = 18.sp
                )
            }
        }
    )
}