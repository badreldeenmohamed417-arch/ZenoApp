package com.example.zeno.features.session

import org.koin.compose.koinInject

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zeno.R
import com.example.zeno.core.ThinkingIndicator
import com.example.zeno.core.ui.modifiers.bounceClickable
import com.example.zeno.core.widgets.ChatBubble
import com.example.zeno.data.local.db.AppDatabase

import com.example.zeno.features.chat.presentation.ChatActionChipData
import kotlinx.coroutines.launch

import com.example.zeno.data.AppColors

private val DarkBG: Color @Composable get() = AppColors.BG
private val CardBG: Color @Composable get() = AppColors.CardBG
private val CardBorder: Color @Composable get() = AppColors.CardBorder
private val LimeAccent: Color @Composable get() = AppColors.LimeAccent
private val TextWhite: Color @Composable get() = AppColors.TextWhite
private val TextMuted: Color @Composable get() = AppColors.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDropUp(
    conversationId: String?,
    onConversationCreated: (String) -> Unit,
    timeLeftStr: String
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val chatRepository = koinInject<com.example.zeno.data.repository.ChatRepository>(org.koin.core.qualifier.named("legacyChat"))

    var currentConversationId by remember { mutableStateOf(conversationId) }
    var messageText by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var showActionMenu by remember { mutableStateOf(false) }
    var selectedActionChip by remember { mutableStateOf<ChatActionChipData?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { _ -> }

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
            .background(DarkBG)
    ) {
        // Handle
        Box(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 4.dp)
                .size(36.dp, 4.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(CardBorder)
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
                Orb(modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.askZeno),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(LimeAccent.copy(alpha = 0.15f))
                    .border(1.dp, LimeAccent.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = timeLeftStr,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = LimeAccent
                )
            }
        }

        HorizontalDivider(color = CardBorder, thickness = 1.dp, modifier = Modifier.padding(top = 8.dp))

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

        // Input Composer Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            if (selectedActionChip != null) {
                Row(
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBG)
                        .border(1.dp, LimeAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = selectedActionChip!!.icon,
                        contentDescription = null,
                        tint = LimeAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedActionChip!!.title,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { selectedActionChip = null }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBG),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sleek Attachment / AI Tools Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(LimeAccent.copy(alpha = 0.15f))
                            .border(1.dp, LimeAccent.copy(alpha = 0.3f), CircleShape)
                            .bounceClickable { showActionMenu = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = null,
                            tint = LimeAccent,
                            modifier = Modifier.size(20.dp)
                        )

                        DropdownMenu(
                            expanded = showActionMenu,
                            onDismissRequest = { showActionMenu = false },
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(CardBG)
                                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        ) {
                            val quizTitle = stringResource(R.string.chat_action_quiz_title)
                            val quizPrompt = stringResource(R.string.auto_str_أنشئ_اختبار_تفاعلي)
                            val handoutTitle = stringResource(R.string.chat_action_handout_title)
                            val handoutPrompt = stringResource(R.string.auto_str_قم_بعمل_ملزمة)

                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.chat_action_image_doc), color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                                leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, tint = LimeAccent, modifier = Modifier.size(18.dp)) },
                                onClick = {
                                    showActionMenu = false
                                    launcher.launch("image/*")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.chat_action_pdf), color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                                leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(18.dp)) },
                                onClick = {
                                    showActionMenu = false
                                    launcher.launch("application/pdf")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.chat_action_quiz), color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                                leadingIcon = { Icon(Icons.Default.Quiz, contentDescription = null, tint = Color(0xFFFB923C), modifier = Modifier.size(18.dp)) },
                                onClick = {
                                    showActionMenu = false
                                    selectedActionChip =
                                        ChatActionChipData(quizTitle, quizPrompt, Icons.Default.Quiz)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.chat_action_handout), color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                                leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = LimeAccent, modifier = Modifier.size(18.dp)) },
                                onClick = {
                                    showActionMenu = false
                                    selectedActionChip =
                                        ChatActionChipData(handoutTitle, handoutPrompt, Icons.Default.AutoAwesome)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    BasicTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 12.dp),
                        enabled = !isSending,
                        textStyle = TextStyle(
                            color = TextWhite,
                            fontSize = 15.sp
                        ),
                        maxLines = 4,
                        cursorBrush = SolidColor(LimeAccent),
                        decorationBox = { innerTextField ->
                            if (messageText.isEmpty() && selectedActionChip == null) {
                                Text(
                                    text = stringResource(R.string.chat_input_hint),
                                    color = TextMuted,
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(LimeAccent)
                            .bounceClickable {
                                val fullText = listOfNotNull(selectedActionChip?.prompt, messageText.trim().ifBlank { null }).joinToString(" ")
                                if (fullText.isNotBlank() && !isSending) {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    selectedActionChip = null
                                    messageText = ""
                                    isSending = true
                                    scope.launch {
                                        try {
                                            if (currentConversationId == null) {
                                                val newConv = chatRepository.createConversation(title = fullText.take(20))
                                                currentConversationId = newConv.id
                                                onConversationCreated(newConv.id)
                                                chatRepository.sendMessage(newConv.id, fullText)
                                            } else {
                                                chatRepository.sendMessage(currentConversationId!!, fullText)
                                            }
                                        } catch (e: Exception) {
                                            if (currentConversationId == null) messageText = fullText
                                        } finally {
                                            isSending = false
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyChatState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(CardBG)
                .border(1.dp, CardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.ic_zeno_logo),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(LimeAccent)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.chat_welcome_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.chat_welcome_subtitle),
            fontSize = 14.sp,
            color = TextMuted,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}
