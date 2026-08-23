package com.example.zeno.core.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.zeno.data.AppColors

@Composable
fun CardFun(
    modifier: Modifier = Modifier,

    backgroundColor: Color = AppColors.Surface,
    contentColor: Color = AppColors.TextPrimary,

    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,

    cornerRadius: Dp = 16.dp,

    contentPadding: Dp = 16.dp,

    content: @Composable ColumnScope.() -> Unit,

    shape: Shape = RoundedCornerShape(cornerRadius)
) {
    Card(
        modifier = modifier,

        shape = shape,

        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),

        border = if (borderWidth > 0.dp) {
            BorderStroke(
                width = borderWidth,
                color = borderColor
            )
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}