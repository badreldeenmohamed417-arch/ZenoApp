package com.example.zeno.core.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.zeno.core.theme.CardFun
import com.example.zeno.data.AppColors

@Composable
fun ChatBubble(
    text: String,
    menuItems: List<ActionMenuItem>
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    val layoutDirection = LocalLayoutDirection.current

    val bubbleShape = if (
        layoutDirection == LayoutDirection.Rtl
    ) {
        RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 16.dp,
            bottomEnd = 3.dp
        )
    } else {
        RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 3.dp,
            bottomEnd = 16.dp
        )
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (
            layoutDirection == LayoutDirection.Rtl
        ) {
            Alignment.CenterEnd
        } else {
            Alignment.CenterStart
        }
    ) {
        CardFun(
            modifier = Modifier.clickable {
                menuExpanded = true
            },
            content = {
                Text(
                    text = text,
                    color = AppColors.UserBubbleText
                )
            },
            backgroundColor = AppColors.UserBubble,
            contentColor = AppColors.UserBubbleText,
            shape = bubbleShape
        )

        ActionMenu(
            expanded = menuExpanded,
            onDismissRequest = {
                menuExpanded = false
            },
            items = menuItems
        )
    }
}