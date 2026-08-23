package com.example.zeno.core

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.zeno.core.theme.CardFun
import com.example.zeno.data.AppColors

// -------------------------------------------------
// "Zeno بيفكر..." indicator
// -------------------------------------------------
@Composable
fun ThinkingIndicator(
    modifier: Modifier = Modifier
) {
    CardFun(
        modifier = modifier,
        backgroundColor = AppColors.Surface,
        contentPadding = 12.dp,
        content = {
            Row(verticalAlignment = Alignment.CenterVertically) {

                // الكرة الصغيرة النابضة (Orb)
                PulsingOrb()

                Text(
                    text = "Zeno بيفكر",
                    color = AppColors.TextMuted,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // النقط الثلاث المتحركة
                BouncingDots()
            }
        }
    )
}

// -------------------------------------------------
// كرة بسيطة بتكبر وتصغر بشكل متكرر (نبضة)
// -------------------------------------------------
@Composable
private fun PulsingOrb() {
    val transition = rememberInfiniteTransition(label = "orbPulse")

    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(14.dp)
            .clip(CircleShape)
            .background(AppColors.Accent)
            .then(Modifier) // مكان مفتوح لو حبيت تضيف Modifier.scale(scale) بعدين
    )
}

// -------------------------------------------------
// ثلاث نقط بتنط الواحدة بعد التانية
// -------------------------------------------------
@Composable
private fun BouncingDots() {
    Row {
        repeat(3) { index ->
            Dot(delayMillis = index * 150)
            androidx.compose.foundation.layout.Spacer(Modifier.width(3.dp))
        }
    }
}

@Composable
private fun Dot(delayMillis: Int) {
    val transition = rememberInfiniteTransition(label = "dot")

    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = delayMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(5.dp)
            .alpha(alpha)
            .clip(CircleShape)
            .background(AppColors.TextFaint)
    )
}