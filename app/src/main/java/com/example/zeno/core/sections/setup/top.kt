package com.example.zeno.core.sections.setup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors

@Composable
fun LetUsKnowYou() {
    Box{
        Column {
            Text(
                text = txt("letUsKnowYou"),
                color = AppColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = txt("letUsKnowYouDescription"),
                color = AppColors.TextMuted,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}