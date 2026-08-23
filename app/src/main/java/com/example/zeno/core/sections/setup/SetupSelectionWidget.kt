package com.example.zeno.core.sections.setup

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.core.theme.CardFun
import com.example.zeno.data.AppColors

@Composable
fun SelectionGroupWidget(
    title: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    CardFun(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = AppColors.SurfaceVariant, // استخدام لون السطح الفرعي المتناسق
        contentPadding = 16.dp,
        cornerRadius = 20.dp,
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = AppColors.TextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )

                options.forEach { option ->
                    val isSelected = option == selectedOption

                    // أنيميشن تحول الألوان عند الاختيار
                    val targetBgColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFF1E1B4B) else AppColors.Surface,
                        animationSpec = tween(durationMillis = 200),
                        label = "bgColorAnimation"
                    )

                    val targetBorderColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFF6366F1) else Color(0xFF1F2937),
                        animationSpec = tween(durationMillis = 200),
                        label = "borderColorAnimation"
                    )

                    val targetTextColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFF818CF8) else AppColors.Black,
                        animationSpec = tween(durationMillis = 200),
                        label = "textColorAnimation"
                    )

                    CardFun(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onOptionSelected(option) },
                        backgroundColor = targetBgColor,
                        borderColor = targetBorderColor,
                        borderWidth = if (isSelected) 1.5.dp else 1.dp,
                        cornerRadius = 14.dp,
                        contentPadding = 14.dp,
                        content = {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                ),
                                color = targetTextColor,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    )
                }
            }
        }
    )
}