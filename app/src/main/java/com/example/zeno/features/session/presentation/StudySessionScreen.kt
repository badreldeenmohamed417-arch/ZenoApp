package com.example.zeno.features.session.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.features.session.ChatDropUp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun StudySessionScreen(
    subjectName: String = "Mathematics",
    topicName: String = "Algebra",
    onStopSession: () -> Unit
) {
    var elapsedSeconds by remember { mutableStateOf(0) }
    var chatOpen by remember { mutableStateOf(false) }
    var conversationId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1.seconds)
            elapsedSeconds++
        }
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = androidx.compose.ui.res.stringResource(com.example.zeno.R.string.auto_str_جلسة_مذاكرة),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subjectName,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = topicName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(64.dp))

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = onStopSession,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(56.dp)
            ) {
                Icon(
                    Icons.Default.Stop,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onError
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    androidx.compose.ui.res.stringResource(com.example.zeno.R.string.stop_session),
                    color = MaterialTheme.colorScheme.onError,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (!chatOpen) {
            FloatingActionButton(
                onClick = { chatOpen = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .size(58.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = androidx.compose.ui.res.stringResource(com.example.zeno.R.string.askZeno),
                    modifier = Modifier.size(25.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = chatOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f))
                        .clickable { chatOpen = false }
                )

                ChatDropUp(
                    conversationId = conversationId,
                    onConversationCreated = { id ->
                        conversationId = id
                    },
                    timeLeftStr = formattedTime,
                    onDismiss = { chatOpen = false },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}
