package com.example.zeno.futures.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zeno.core.ThinkingIndicator
import com.example.zeno.core.widgets.ChatBubble
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors
import com.example.zeno.data.local.db.AppDatabase
import com.example.zeno.data.model.server.MessageResponse
import com.example.zeno.data.repository.ChatRepository
import com.example.zeno.futures.session.Orb
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val chatRepository = remember {
        ChatRepository(AppDatabase.getDatabase(context).chatDao())
    }
    
    var currentConversationId by remember { mutableStateOf(conversationId) }
    var messageText by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    val messages = remember { mutableStateListOf<MessageResponse>() }
    
    val localMessages by chatRepository.getLocalMessages(currentConversationId ?: "").collectAsStateWithLifecycle(initialValue = emptyList())

    val noInternetMsg = stringResource(com.example.zeno.R.string.noInternetSubtitle)
    val aiErrorMsg = stringResource(com.example.zeno.R.string.aiErrorTitle)

    LaunchedEffect(currentConversationId) {
        if (currentConversationId != null) {
            try {
                chatRepository.getConversation(currentConversationId!!)
            } catch (e: Exception) {
                // Fallback to local messages if offline
            }
        }
    }
    
    val displayMessages = if (currentConversationId == null) messages else localMessages

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Orb(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (currentConversationId == null) txt("newConversation") else stringResource(com.example.zeno.R.string.app_name),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.BG,
                    titleContentColor = AppColors.TextPrimary,
                    navigationIconContentColor = AppColors.TextPrimary
                )
            )
        },
        bottomBar = {
            ChatComposer(
                messageText = messageText,
                isSending = isSending,
                onMessageChange = { messageText = it },
                onSend = {
                    if (messageText.isNotBlank() && !isSending) {
                        val text = messageText
                        messageText = ""
                        isSending = true
                        
                        scope.launch {
                            try {
                                if (currentConversationId == null) {
                                    val newConv = chatRepository.createConversation(title = text.take(20))
                                    currentConversationId = newConv.id
                                    chatRepository.sendMessage(newConv.id, text)
                                } else {
                                    chatRepository.sendMessage(currentConversationId!!, text)
                                }
                            } catch (e: Exception) {
                                val errorMsg = when (e) {
                                    is java.net.UnknownHostException, is java.net.ConnectException -> noInternetMsg
                                    else -> aiErrorMsg
                                }
                                snackbarHostState.showSnackbar(errorMsg)
                                // If it was a new conversation that failed to create, reset message text
                                if (currentConversationId == null) messageText = text
                            } finally {
                                isSending = false
                            }
                        }
                    }
                }
            )
        },
        containerColor = AppColors.BG
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (displayMessages.isEmpty() && !isSending) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Orb(modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = txt("startChatWithZeno"), color = AppColors.TextMuted)
                        }
                    }
                }
            } else {
                items(displayMessages, key = { it.id }) { msg ->
                    ChatBubble(msg)
                }
                
                if (isSending) {
                    item {
                        ThinkingIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun ChatComposer(
    messageText: String,
    isSending: Boolean,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(AppColors.Surface)
                .border(1.5.dp, AppColors.UnfocusedBorder, RoundedCornerShape(28.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText,
                onValueChange = onMessageChange,
                placeholder = { Text(txt("chatInputPlaceholder"), color = AppColors.TextFaint, fontSize = 15.sp) },
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
                textStyle = LocalTextStyle.current.copy(fontSize = 15.sp)
            )

            IconButton(
                onClick = onSend,
                modifier = Modifier.size(40.dp).background(AppColors.Accent, CircleShape),
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
