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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors
import com.example.zeno.data.model.server.MessageResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDropUp(
    conversationId: String?,
    onConversationCreated: (String) -> Unit,
    timeLeftStr: String
) {
    var messageText by remember { mutableStateOf("") }
    // Mock messages for now
    val messages = remember { mutableStateListOf<MessageResponse>() }

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
            if (messages.isEmpty()) {
                item {
                    EmptyChatState()
                }
            } else {
                items(messages) { msg ->
                    // Use existing chat bubbles here if available
                    ChatMessageItem(msg)
                }
            }
        }

        // Composer
        Box(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 14.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppColors.Surface)
                    .border(1.5.dp, AppColors.UnfocusedBorder, RoundedCornerShape(24.dp))
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* Add logic */ },
                    modifier = Modifier.size(36.dp).background(AppColors.SurfaceVariant, CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = AppColors.TextMuted)
                }
                
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text(txt("composerPlaceholder"), color = AppColors.TextFaint, fontSize = 14.5.sp) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.5.sp)
                )

                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            // Send logic
                            messageText = ""
                        }
                    },
                    modifier = Modifier.size(36.dp).background(AppColors.Accent, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = AppColors.AccentInk)
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
        Text(text = "أهلاً بيك 👋", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = "اسأل Zeno عن أي حاجة في منهجك.", fontSize = 13.5.sp, color = AppColors.TextMuted)
    }
}

@Composable
fun ChatMessageItem(message: MessageResponse) {
    // Simple message item for now
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.role == "user") Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(if (message.role == "user") AppColors.UserBubble else AppColors.SurfaceVariant)
                .padding(13.dp)
        ) {
            Text(text = message.content, color = if (message.role == "user") AppColors.UserBubbleText else AppColors.TextPrimary)
        }
    }
}
