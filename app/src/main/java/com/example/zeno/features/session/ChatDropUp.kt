package com.example.zeno.futures.session

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zeno.core.ThinkingIndicator
import com.example.zeno.core.widgets.ChatBubble
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors
import com.example.zeno.data.local.db.AppDatabase
import com.example.zeno.data.repository.ChatRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDropUp(
    conversationId: String?,
    onConversationCreated: (String) -> Unit,
    timeLeftStr: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val chatRepository = remember {
        ChatRepository(AppDatabase.getDatabase(context).chatDao())
    }
    
    var currentConversationId by remember { mutableStateOf(conversationId) }
    var messageText by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    
    val localMessages by chatRepository.getLocalMessages(currentConversationId ?: "").collectAsStateWithLifecycle(initialValue = emptyList())

    LaunchedEffect(currentConversationId) {
        if (currentConversationId != null) {
            try {
                chatRepository.getConversation(currentConversationId!!)
            } catch (e: Exception) {
                // Offline
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight(0.85f)
            .fillMaxWidth()
            .background(AppColors.BG)
    ) {
        // Handle
        Box(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 4.dp)
                .size(36.dp, 4.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(AppColors.SurfaceVariant2)
                .align(Alignment.CenterHorizontally)
        )

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Orb(modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = txt("askZeno"),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMuted
                )
            }
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.AccentSoft)
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = timeLeftStr,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Accent
                )
            }
        }

        // Chat Body
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (localMessages.isEmpty() && !isSending) {
                item {
                    EmptyChatState()
                }
            } else {
                items(localMessages, key = { it.id }) { msg ->
                    ChatBubble(msg)
                }
                
                if (isSending) {
                    item {
                        ThinkingIndicator()
                    }
                }
            }
        }

        // Composer
        Box(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 14.dp)
                .fillMaxWidth()
                .imePadding()
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppColors.Surface)
                    .border(1.5.dp, AppColors.UnfocusedBorder, RoundedCornerShape(24.dp))
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text(txt("chatInputPlaceholder"), color = AppColors.TextFaint, fontSize = 14.5.sp) },
                    modifier = Modifier.weight(1f),
                    enabled = !isSending,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = AppColors.TextPrimary,
                        unfocusedTextColor = AppColors.TextPrimary,
                        disabledContainerColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.5.sp)
                )

                IconButton(
                    onClick = {
                        if (messageText.isNotBlank() && !isSending) {
                            val text = messageText
                            messageText = ""
                            isSending = true
                            scope.launch {
                                try {
                                    if (currentConversationId == null) {
                                        val newConv = chatRepository.createConversation(title = text.take(20))
                                        currentConversationId = newConv.id
                                        onConversationCreated(newConv.id)
                                        chatRepository.sendMessage(newConv.id, text)
                                    } else {
                                        chatRepository.sendMessage(currentConversationId!!, text)
                                    }
                                } catch (e: Exception) {
                                    if (currentConversationId == null) messageText = text
                                } finally {
                                    isSending = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.size(36.dp).background(AppColors.Accent, CircleShape),
                    enabled = messageText.isNotBlank() && !isSending
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = if (messageText.isNotBlank() && !isSending) AppColors.AccentInk else AppColors.TextFaint
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyChatState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Orb(modifier = Modifier.size(72.dp))
        Spacer(modifier = Modifier.height(18.dp))
        Text(text = txt("welcome_greeting"), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = txt("ask_zeno_intro"), fontSize = 13.5.sp, color = AppColors.TextMuted)
    }
}
