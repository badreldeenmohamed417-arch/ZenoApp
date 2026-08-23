package com.example.zeno.core.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.zeno.data.AppColors

@Composable
fun ButtonFun(
    onClick: () -> Unit,
    items: @Composable () -> Unit,

    backgroundColor: Color = AppColors.Accent,
    contentColor: Color = AppColors.AccentInk,
    disabledBackgroundColor: Color = AppColors.SurfaceVariant,
    disabledContentColor: Color = AppColors.TextFaint,

    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,
    cornerRadius: Dp = 12.dp,
    enabled: Boolean = true,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,

        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = disabledBackgroundColor,
            disabledContentColor = disabledContentColor
        ),

        border = if (borderWidth > 0.dp) {
            BorderStroke(
                width = borderWidth,
                color = borderColor
            )
        } else {
            null
        },

        shape = RoundedCornerShape(cornerRadius),

        contentPadding = ButtonDefaults.ContentPadding
    ) {
        items()
    }
}