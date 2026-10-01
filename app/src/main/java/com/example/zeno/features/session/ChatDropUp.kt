package com.example.zeno.features.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.R
import com.example.zeno.core.NetworkUtils
import com.example.zeno.core.widgets.ChatBubble
import com.example.zeno.data.model.server.MessageResponse
import com.example.zeno.data.AppColors
import com.example.zeno.features.chat.data.repository.ChatRepository
import com.example.zeno.features.chat.domain.ChatMessage
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import java.util.UUID

@Composable
fun ChatDropUp(
    conversationId: String?,
    onConversationCreated: (String) -> Unit,
    timeLeftStr: String,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val repository: ChatRepository = get()
    val scope = rememberCoroutineScope()
    var currentId by remember { mutableStateOf(conversationId) }
    var input by remember { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }
    var messages by remember { mutableStateOf(emptyList<com.example.zeno.features.chat.domain.ChatMessage>()) }

    LaunchedEffect(currentId) {
        val id = currentId ?: return@LaunchedEffect
        val result = repository.getConversationDetails(id)
        if (result.isSuccess) {
            messages = result.getOrNull()?.messages?.map {
                com.example.zeno.features.chat.domain.ChatMessage(
                    id = it.id,
                    text = it.content,
                    isUser = it.role == "user",
                    timestamp = System.currentTimeMillis()
                )
            } ?: emptyList()
        }
    }

    Column(
        modifier = modifier.fillMaxWidth().fillMaxHeight(0.85f).background(AppColors.BG)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.chat_history_drawer_title), style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(timeLeftStr, color = AppColors.TextMuted, fontSize = 12.sp)
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.chat_menu_delete)) }
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg -> ChatBubble(MessageResponse(id = msg.id, conversationId = currentId ?: "", role = if (msg.isUser) "user" else "assistant", content = msg.text, createdAt = "")) }
            if (sending) item { 
                val stages = listOf(
                    R.string.chat_status_thinking,
                    R.string.chat_status_understanding,
                    R.string.chat_status_searching_books,
                    R.string.chat_status_building_answer
                )
                var currentStageIdx by remember { mutableIntStateOf(0) }
                LaunchedEffect(Unit) {
                    while(true) {
                        kotlinx.coroutines.delay(2500)
                        currentStageIdx = (currentStageIdx + 1) % stages.size
                    }
                }
                
                val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition()
                val translateAnim by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1000f,
                    animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                        animation = androidx.compose.animation.core.tween(durationMillis = 1500, easing = androidx.compose.animation.core.LinearEasing),
                        repeatMode = androidx.compose.animation.core.RepeatMode.Restart
                    )
                )

                val brush = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(
                        AppColors.TextMuted.copy(alpha = 0.5f),
                        Color.White,
                        AppColors.TextMuted.copy(alpha = 0.5f)
                    ),
                    start = androidx.compose.ui.geometry.Offset(translateAnim - 300f, 0f),
                    end = androidx.compose.ui.geometry.Offset(translateAnim, 0f)
                )
                
                Text(
                    stringResource(stages[currentStageIdx]), 
                    style = androidx.compose.ui.text.TextStyle(brush = brush),
                    fontSize = 13.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ) 
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = input,
                onValueChange = { input = it },
                enabled = !sending,
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(R.string.chat_input_hint)) }
            )
            IconButton(
                onClick = {
                    val text = input.trim()
                    if (text.isEmpty() || sending) return@IconButton
                    input = ""
                    sending = true
                    val userMsg = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        text = text,
                        isUser = true,
                        timestamp = System.currentTimeMillis()
                    )
                    messages = messages + userMsg
                    scope.launch {
                        try {
                            var id = currentId
                            if (id == null) {
                                id = repository.createConversation(text.take(40)).getOrNull()?.id
                                if (id != null) {
                                    currentId = id
                                    onConversationCreated(id)
                                }
                            }
                            if (id != null) {
                                repository.cacheUserMessage(id, userMsg.id, text)
                                var result = repository.sendConversationMessage(id, text)
                                if (result.isFailure) {
                                    val errMsg = result.exceptionOrNull()?.message.orEmpty()
                                    if (errMsg.contains("404") || errMsg.contains("not found", ignoreCase = true)) {
                                        id = repository.createConversation(text.take(40)).getOrNull()?.id
                                        if (id != null) {
                                            currentId = id
                                            onConversationCreated(id)
                                            result = repository.sendConversationMessage(id, text)
                                        }
                                    }
                                }
                                if (result.isFailure) {
                                    val fallbackResult = repository.sendMessage(text)
                                    if (fallbackResult.isSuccess) {
                                        val replyText = fallbackResult.getOrNull()?.reply ?: ""
                                        if (replyText.isNotBlank()) {
                                            messages = messages + ChatMessage(
                                                id = UUID.randomUUID().toString(),
                                                text = replyText,
                                                isUser = false,
                                                timestamp = System.currentTimeMillis()
                                            )
                                            return@launch
                                        }
                                    }
                                }
                                if (result.isSuccess) {
                                    val reply = result.getOrNull()
                                    if (reply != null) {
                                        messages = messages + ChatMessage(
                                            id = reply.id,
                                            text = reply.content,
                                            isUser = false,
                                            timestamp = System.currentTimeMillis()
                                        )
                                    }
                                } else {
                                    val errText = result.exceptionOrNull()?.let { NetworkUtils.getErrorMessage(it) } ?: "فشل إرسال الرسالة"
                                    messages = messages + ChatMessage(
                                        id = UUID.randomUUID().toString(),
                                        text = errText,
                                        isUser = false,
                                        timestamp = System.currentTimeMillis(),
                                        isError = true,
                                        failedText = text
                                    )
                                }
                            } else {
                                messages = messages + ChatMessage(
                                    id = UUID.randomUUID().toString(),
                                    text = "حدث خطأ أثناء إنشاء المحادثة.",
                                    isUser = false,
                                    timestamp = System.currentTimeMillis(),
                                    isError = true,
                                    failedText = text
                                )
                            }
                        } finally {
                            sending = false
                        }
                    }
                },
                enabled = input.isNotBlank() && !sending
            ) { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null) }
        }
    }
}
