package com.example.zeno.core.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.data.AppColors
import com.example.zeno.data.model.server.MessageResponse

@Composable
fun ChatBubble(message: MessageResponse) {
    val isUser = message.role == "user"
    val layoutDirection = LocalLayoutDirection.current
    val alignment = if (isUser) {
        if (layoutDirection == LayoutDirection.Rtl) Alignment.Start else Alignment.End
    } else {
        if (layoutDirection == LayoutDirection.Rtl) Alignment.End else Alignment.Start
    }
    
    val userTailOnStart = layoutDirection == LayoutDirection.Rtl
    val bottomStart = if (isUser == userTailOnStart) 4.dp else 18.dp
    val bottomEnd = if (isUser != userTailOnStart) 4.dp else 18.dp

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = bottomStart,
                        bottomEnd = bottomEnd
                    )
                )
                .background(if (isUser) AppColors.Accent else AppColors.SurfaceVariant)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.content,
                color = if (isUser) AppColors.AccentInk else AppColors.TextPrimary,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        }
    }
}
