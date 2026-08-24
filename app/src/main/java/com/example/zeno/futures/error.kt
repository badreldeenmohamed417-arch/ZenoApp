package com.example.zeno.futures

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.data.AppColors
import kotlinx.coroutines.delay

enum class MessageType {
    ERROR,
    SUCCESS,
    WARNING,
    INFO
}

@Composable
fun BackBU(onBack: ()-> Unit, text: String){
    val context = LocalContext.current
    Row(modifier = Modifier.fillMaxWidth()) {
        FloatingActionButton(
            onClick = { onBack() },
            containerColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .size(45.dp)
                .border(0.5.dp, Color.Transparent, CircleShape),
            elevation = FloatingActionButtonDefaults.elevation(2.dp)
        ) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", modifier = Modifier.size(25.dp), tint = AppColors.Surface)
        }
        Text(
            text = text,
            color = Color.Black,
            modifier = Modifier.weight(1f).padding(12.dp),
            maxLines = 1,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TopMessage(
    message: String,
    visible: Boolean,
    type: MessageType,
    onDismiss: () -> Unit = {},
    durationMillis: Long = 3000L
) {
    // إخفاء الرسالة تلقائياً بعد مرور الوقت المحدد
    LaunchedEffect(visible) {
        if (visible) {
            delay(durationMillis)
            onDismiss()
        }
    }

    val borderColor = when (type) {
        MessageType.ERROR -> Color(0xFFFF5252).copy(alpha = 0.45f)
        MessageType.SUCCESS -> Color(0xFF69F0AE).copy(alpha = 0.45f)
        MessageType.WARNING -> Color(0xFFFFD740).copy(alpha = 0.45f)
        MessageType.INFO -> Color(0xFF40C4FF).copy(alpha = 0.45f)
    }

    val backgroundGlass = when (type) {
        MessageType.ERROR -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFFF1744).copy(alpha = 0.35f),
                Color(0xFFD50000).copy(alpha = 0.20f)
            )
        )
        MessageType.SUCCESS -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF00E676).copy(alpha = 0.35f),
                Color(0xFF00C853).copy(alpha = 0.20f)
            )
        )
        MessageType.WARNING -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFD740).copy(alpha = 0.35f),
                Color(0xFFFF6D00).copy(alpha = 0.20f)
            )
        )
        MessageType.INFO -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF40C4FF).copy(alpha = 0.35f),
                Color(0xFF0091EA).copy(alpha = 0.20f)
            )
        )
    }

    val iconTint = when (type) {
        MessageType.ERROR -> Color(0xFFFA3C2B)
        MessageType.SUCCESS -> Color(0xFFB9F6CA)
        MessageType.WARNING -> Color(0xFFFFE57F)
        MessageType.INFO -> Color(0xFF80D8FF)
    }

    val icon = when (type) {
        MessageType.ERROR -> Icons.Default.Error
        MessageType.SUCCESS -> Icons.Default.Check
        MessageType.WARNING -> Icons.Default.Warning
        MessageType.INFO -> Icons.Default.Info
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            initialOffsetY = { -it }
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(
            animationSpec = tween(250, easing = FastOutLinearInEasing),
            targetOffsetY = { -it }
        ) + fadeOut(animationSpec = tween(200))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            borderColor,
                            borderColor.copy(alpha = 0.15f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    backgroundGlass,
                    RoundedCornerShape(16.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = message,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
