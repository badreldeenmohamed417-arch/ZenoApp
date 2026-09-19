package com.example.zeno.core.widgets

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.core.txt
import com.example.zeno.core.ui.modifiers.bounceClickable
import com.example.zeno.data.AppColors

enum class BottomNavItem(val title: String, val icon: ImageVector) {
    HOME("", Icons.Outlined.Home),
    CHATS("", Icons.AutoMirrored.Outlined.Chat),
    SETTINGS("", Icons.Outlined.Settings)
}

@Composable
fun <T> FloatingZenoBottomBar(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    getItemIcon: (T) -> ImageVector,
    getItemTitle: @Composable (T) -> String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = AppColors.Accent.copy(alpha = 0.25f),
                    ambientColor = AppColors.TextPrimary.copy(alpha = 0.12f)
                ),
            shape = RoundedCornerShape(32.dp),
            color = AppColors.Surface.copy(alpha = 0.95f),
            border = BorderStroke(
                width = 1.dp,
                color = AppColors.UnfocusedBorder
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = item == selectedItem
                    val title = getItemTitle(item)
                    val icon = getItemIcon(item)

                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) AppColors.AccentSoft else Color.Transparent,
                        animationSpec = tween(durationMillis = 250),
                        label = "navBg"
                    )

                    val contentColor by animateColorAsState(
                        targetValue = if (isSelected) AppColors.Accent else AppColors.TextMuted,
                        animationSpec = tween(durationMillis = 250),
                        label = "navContent"
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(bgColor)
                            .bounceClickable { onItemSelected(item) }
                            .padding(
                                horizontal = if (isSelected && title.isNotBlank()) 16.dp else 12.dp,
                                vertical = 10.dp
                            )
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = contentColor,
                            modifier = Modifier.size(22.dp)
                        )

                        if (isSelected && title.isNotBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = contentColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ZenoBottomNavigationBar(
    selectedTab: BottomNavItem = BottomNavItem.HOME,
    onTabSelected: (BottomNavItem) -> Unit = {}
) {
    val items = BottomNavItem.entries.toList()

    FloatingZenoBottomBar(
        items = items,
        selectedItem = selectedTab,
        onItemSelected = onTabSelected,
        getItemIcon = { it.icon },
        getItemTitle = {
            when (it) {
                BottomNavItem.HOME -> txt("navHome")
                BottomNavItem.CHATS -> txt("navConversations")
                BottomNavItem.SETTINGS -> txt("navSettings")
            }
        }
    )
}
