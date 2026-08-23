package com.example.zeno.core.sections.setup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    // الكارت الخارجي الرئيسي
    CardFun(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color(0xFFA7B5BFF),
        contentPadding = 16.dp,
        cornerRadius = 20.dp,
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // عنوان المجموعة
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = AppColors.TextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End // لتوجيه النص لليمين (RTL)
                )

                // عرض قائمة الاختيارات
                options.forEach { option ->
                    val isSelected = option == selectedOption

                    CardFun(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(option) },
                        backgroundColor = if (isSelected) Color(0xFFECEBFF) else Color.White,
                        borderColor = if (isSelected) Color(0xFF7C63FF) else Color.Transparent,
                        borderWidth = if (isSelected) 1.5.dp else 0.dp,
                        cornerRadius = 14.dp,
                        contentPadding = 16.dp,
                        content = {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isSelected) Color(0xFF7C63FF) else Color.Black,
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