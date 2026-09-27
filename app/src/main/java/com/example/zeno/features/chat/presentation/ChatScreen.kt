package com.example.zeno.features.chat.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.zeno.R
import com.example.zeno.core.theme.Emerald300
import com.example.zeno.core.theme.Emerald500
import com.example.zeno.core.theme.Emerald700
import com.example.zeno.core.ui.modifiers.animatedGradientBackground
import com.example.zeno.core.ui.modifiers.bounceClickable
import com.example.zeno.core.widgets.ZenoTextField
import com.example.zeno.features.chat.domain.ChatMessage

import com.example.zeno.features.chat.presentation.ChatViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Chat Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // AI Indicator Dot
                Box(
                    modifier = Modifier
                        .padding(start = 24.dp)
                        .size(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .animatedGradientBackground(listOf(Emerald300, Emerald700))
                )
                Text(
                    text = stringResource(id = R.string.nav_chat),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Message List
        Box(modifier = Modifier.weight(1f)) {
            if (messages.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.chat_empty_state_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.chat_empty_state_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    reverseLayout = false
                ) {
                    items(messages) { message ->
                        ChatBubble(message = message)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    if (isTyping) {
                        item {
                            TypingIndicator()
                        }
                    }
                }
            }
        }

        // Input Area
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 16.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ZenoTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = stringResource(id = R.string.chat_input_hint)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Emerald500)
                        .clip(RoundedCornerShape(16.dp))
                        .animatedGradientBackground(listOf(Emerald500, Emerald700))
                        .bounceClickable {
                            if (inputText.isNotBlank() && !isTyping) {
                                viewModel.sendMessage(inputText.trim())
                                inputText = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (isUser) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    }

    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        contentAlignment = alignment
    ) {
        if (isUser) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = shape,
                shadowElevation = 2.dp,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        } else {
            // Zeno AI Bubble
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(shape)
                    .animatedGradientBackground(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface))
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp))
                .animatedGradientBackground(listOf(Emerald300.copy(alpha = 0.2f), Emerald500.copy(alpha = 0.2f)))
        ) {
            Text(
                text = stringResource(id = R.string.chat_typing_indicator),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Emerald700,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

