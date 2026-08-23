package com.example.zeno.core

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.R
import com.example.zeno.data.AppColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreenContent(
    onFinished: () -> Unit
) {
    // نستنى شوية وقت بعدين ننتقل للشاشة اللي بعدها
    LaunchedEffect(Unit) {
        delay(1500)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BG),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // اللوجو من drawable
            Image(
                painter = painterResource(id = R.drawable.zeno_logo),
                contentDescription = "Zeno Logo",
                modifier = Modifier.size(72.dp)
            )

            Text(
                text = "Zeno",
                color = AppColors.TextPrimary,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 18.dp)
            )

            Text(
                text = "مدرسك الذكي",
                color = AppColors.TextMuted,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        Text(
            text = "by NexorAI",
            color = AppColors.TextFaint,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 44.dp)
        )
    }
}