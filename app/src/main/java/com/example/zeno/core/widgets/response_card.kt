package com.example.zeno.core.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.zeno.core.theme.CardFun
import com.example.zeno.data.AppColors

@Composable
fun ResponseCard(
    answer: String,
    onShowExplanation: () -> Unit,
    modifier: Modifier = Modifier
) {
    CardFun(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color.Transparent,
        contentColor = AppColors.TextPrimary,
        cornerRadius = 16.dp,
        content = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // الإجابة
                Text(
                    text = answer,
                    color = AppColors.TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                // زر عرض الشرح
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onShowExplanation()
                        }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "عرض الشرح",
                        tint = AppColors.TextPrimary
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "عرض الشرح",
                        color = AppColors.TextPrimary
                    )
                }
            }
        }
    )
}