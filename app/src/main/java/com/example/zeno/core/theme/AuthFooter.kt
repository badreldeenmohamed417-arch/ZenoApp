package com.example.zeno.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.data.AppColors

@Composable
fun AuthFooterFun(
    text: String,
    actionText: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onActionClick
        ) {
            Text(
                text = actionText,
                color = AppColors.Accent,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )
        }

        Text(
            text = text,
            color = AppColors.TextMuted,
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            fontSize = 15.sp
        )
    }
}