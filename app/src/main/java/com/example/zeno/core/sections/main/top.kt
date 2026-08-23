package com.example.zeno.core.sections.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.R
import com.example.zeno.data.AppColors

@Composable
fun TopSectionMainScreen(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.zeno_logo),
            contentDescription = "Zeno Logo",
            modifier = Modifier.size(72.dp)
        )
        Text(
            text = "eno",
            color = AppColors.Accent,
            fontSize = 35.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.offset(x = (-15).dp)
        )
    }
}