package com.example.zeno.features.chat.presentation

import com.example.zeno.core.txt

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.R
import com.example.zeno.core.ui.modifiers.bounceClickable
import com.example.zeno.core.widgets.PdfHandoutCard
import com.example.zeno.core.widgets.ZenoMarkdownText
import com.example.zeno.data.local.UserManager
import com.example.zeno.features.chat.domain.ChatMessage
import kotlinx.coroutines.launch

import com.example.zeno.data.AppColors

private val DarkBG: Color @Composable get() = AppColors.BG
private val CardBG: Color @Composable get() = AppColors.CardBG
private val CardBorder: Color @Composable get() = AppColors.CardBorder
private val LimeAccent: Color @Composable get() = AppColors.LimeAccent
private val TextWhite: Color @Composable get() = AppColors.TextWhite
private val TextMuted: Color @Composable get() = AppColors.TextMuted
private val OnlineGreen = Color(0xFF22C55E)

data class ChatActionChipData(
    val title: String,
    val prompt: String,
    val icon: ImageVector
)

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onNavigateToUpgrade: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val isLoadingChat by viewModel.isLoadingChat.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val activeTitle by viewModel.activeTitle.collectAsState()

    var inputText by remember { mutableStateOf("") }
    
    var messageToReport by remember { mutableStateOf<ChatMessage?>(null) }
    var selectedReportReason by remember { mutableStateOf("") }
    
    val reportReasons = listOf(
        "محتوى غير لائق",
        "معلومات خاطئة",
        "محتوى مزعج (Spam)",
        "أخرى"
    )
    var showActionMenu by remember { mutableStateOf(false) }
    var selectedActionChip by remember { mutableStateOf<ChatActionChipData?>(null) }
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.uploadFile(it, contentResolver) { extractedText ->
                inputText = extractedText
            }
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(320.dp),
                drawerContainerColor = DarkBG,
                drawerContentColor = TextWhite
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Drawer Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CardBG)
                                .clickable { scope.launch { drawerState.close() } },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = TextWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = stringResource(R.string.chat_history_drawer_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // New Chat Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardBG)
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                            .bounceClickable {
                                viewModel.clearChat()
                                scope.launch { drawerState.close() }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = TextWhite,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.chat_new_button),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Conversation History List
                    var selectedForAction by remember { mutableStateOf<String?>(null) }
                    var renameDialogVisible by remember { mutableStateOf(false) }
                    var renameText by remember { mutableStateOf("") }
                    var deleteConfirmDialogVisible by remember { mutableStateOf(false) }

                    if (renameDialogVisible && selectedForAction != null) {
                        AlertDialog(
                            onDismissRequest = { renameDialogVisible = false },
                            containerColor = CardBG,
                            shape = RoundedCornerShape(20.dp),
                            title = { Text(stringResource(R.string.chat_rename_dialog_title), color = TextWhite, fontWeight = FontWeight.Bold) },
                            text = {
                                OutlinedTextField(
                                    value = renameText,
                                    onValueChange = { renameText = it },
                                    singleLine = true,
                                    textStyle = TextStyle(color = TextWhite),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = LimeAccent,
                                        unfocusedBorderColor = CardBorder
                                    )
                                )
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    viewModel.renameConversation(selectedForAction!!, renameText)
                                    renameDialogVisible = false
                                }) {
                                    Text(stringResource(R.string.save_button), color = LimeAccent, fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { renameDialogVisible = false }) {
                                    Text(stringResource(R.string.cancel_button), color = TextMuted)
                                }
                            }
                        )
                    }

                    if (deleteConfirmDialogVisible && selectedForAction != null) {
                        AlertDialog(
                            onDismissRequest = { deleteConfirmDialogVisible = false },
                            containerColor = CardBG,
                            shape = RoundedCornerShape(20.dp),
                            title = {
                                Text(
                                    text = stringResource(R.string.chat_delete_dialog_title),
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Text(
                                    text = stringResource(R.string.chat_delete_dialog_message),
                                    color = TextMuted,
                                    fontSize = 14.sp
                                )
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    val idToDelete = selectedForAction!!
                                    deleteConfirmDialogVisible = false
                                    viewModel.deleteConversation(idToDelete)
                                }) {
                                    Text(
                                        text = stringResource(R.string.chat_delete_confirm_button),
                                        color = Color(0xFFEF4444),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { deleteConfirmDialogVisible = false }) {
                                    Text(
                                        text = stringResource(R.string.cancel_button),
                                        color = TextMuted
                                    )
                                }
                            }
                        )
                    }

                    if (conversations.isEmpty()) {
                        Text(
                            text = stringResource(R.string.chat_no_previous_conversations),
                            fontSize = 13.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(conversations) { conv ->
                                var showMenu by remember { mutableStateOf(false) }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CardBG)
                                        .pointerInput(conv.id) {
                                            detectTapGestures(
                                                onTap = {
                                                    viewModel.loadConversation(conv.id)
                                                    scope.launch { drawerState.close() }
                                                },
                                                onLongPress = {
                                                    showMenu = true
                                                }
                                            )
                                        }
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = conv.title ?: stringResource(R.string.chat_new_button),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextWhite,
                                                maxLines = 1
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = conv.createdAt.split("T").firstOrNull() ?: "",
                                                fontSize = 11.sp,
                                                color = TextMuted
                                            )
                                        }

                                        // 3-dots Menu trigger
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .clickable { showMenu = true },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MoreVert,
                                                contentDescription = null,
                                                tint = TextMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false },
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(CardBG)
                                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.chat_menu_rename), color = TextWhite, fontSize = 13.sp) },
                                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = LimeAccent, modifier = Modifier.size(18.dp)) },
                                            onClick = {
                                                showMenu = false
                                                selectedForAction = conv.id
                                                renameText = conv.title ?: ""
                                                renameDialogVisible = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.chat_menu_archive), color = TextWhite, fontSize = 13.sp) },
                                            leadingIcon = { Icon(Icons.Default.Archive, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(18.dp)) },
                                            onClick = {
                                                showMenu = false
                                                selectedForAction = conv.id
                                                deleteConfirmDialogVisible = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.chat_menu_delete), color = Color(0xFFEF4444), fontSize = 13.sp) },
                                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp)) },
                                            onClick = {
                                                showMenu = false
                                                selectedForAction = conv.id
                                                deleteConfirmDialogVisible = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBG)
        ) {
            // Chat Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBG)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // AI Badge
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CardBG),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.app_name),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    }

                    // Conversation Title
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val displayTitle = if (activeTitle.isBlank() || activeTitle == txt("chat_new_button") || activeTitle == "New Chat") {
                            stringResource(R.string.chat_new_button)
                        } else {
                            activeTitle
                        }
                        Text(
                            text = displayTitle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(OnlineGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.chat_online_status),
                                fontSize = 11.sp,
                                color = OnlineGreen
                            )
                        }
                    }

                    // Drawer Toggle Icon
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CardBG)
                            .clickable { scope.launch { drawerState.open() } },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = CardBorder, thickness = 1.dp)

            // Message Area
            Box(modifier = Modifier.weight(1f)) {
                if (isLoadingChat) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = LimeAccent,
                            modifier = Modifier.size(36.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.chat_loading_conversation),
                            fontSize = 13.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else if (messages.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.Center,
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
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(messages) { message ->
                            StyledChatBubble(
                                message = message,
                                onRetry = { failedText -> viewModel.retryMessage(failedText) },
                                onUpgradeClick = onNavigateToUpgrade,
                                onReport = { messageToReport = it }
                            )
                        }
                        if (isTyping) {
                            item {
                                TypingIndicator()
                            }
                        }
                    }
                }
            }

            // Input Bar Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
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
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = LimeAccent,
                                modifier = Modifier.size(20.dp)
                            )

                            DropdownMenu(
                                expanded = showActionMenu,
                                onDismissRequest = { showActionMenu = false },
                                modifier = Modifier
                                    .widthIn(min = 260.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(CardBG)
                                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                                    .padding(vertical = 4.dp)
                            ) {
                                val userManager = remember { UserManager(context) }
                                val isPro = userManager.isPro()
                                val availableTokens = userManager.getAvailableTokens()
                                val isQuizAllowed = isPro && availableTokens >= 50
                                val isHandoutAllowed = isPro && availableTokens >= 100

                                val quizTitle = stringResource(R.string.chat_action_quiz_title)
                                val quizPrompt = stringResource(R.string.auto_str_أنشئ_اختبار_تفاعلي)
                                val handoutTitle = stringResource(R.string.chat_action_handout_title)
                                val handoutPrompt = stringResource(R.string.auto_str_قم_بعمل_ملزمة)

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(R.string.chat_action_image_doc),
                                            color = TextWhite,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = null,
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                                    onClick = {
                                        showActionMenu = false
                                        launcher.launch("image/*")
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(R.string.chat_action_pdf),
                                            color = TextWhite,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.PictureAsPdf,
                                            contentDescription = null,
                                            tint = Color(0xFF60A5FA),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                                    onClick = {
                                        showActionMenu = false
                                        launcher.launch("application/pdf")
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = stringResource(R.string.chat_action_quiz),
                                                color = if (isQuizAllowed) TextWhite else TextMuted,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            if (!isPro) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(0xFFEAB308).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                        .border(0.5.dp, Color(0xFFEAB308), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = stringResource(R.string.chat_action_pro_badge),
                                                        color = Color(0xFFFDE047),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            } else if (availableTokens < 50) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(0xFFEF4444).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                        .border(0.5.dp, Color(0xFFEF4444), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = stringResource(R.string.chat_action_no_tokens_badge),
                                                        color = Color(0xFFFCA5A5),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Quiz,
                                            contentDescription = null,
                                            tint = if (isQuizAllowed) Color(0xFFFB923C) else TextMuted,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                                    enabled = isQuizAllowed,
                                    onClick = {
                                        showActionMenu = false
                                        if (!isPro) {
                                            Toast.makeText(context, context.getString(R.string.chat_action_pro_required_toast), Toast.LENGTH_SHORT).show()
                                        } else if (availableTokens < 50) {
                                            Toast.makeText(context, context.getString(R.string.chat_action_insufficient_tokens_toast), Toast.LENGTH_SHORT).show()
                                        } else {
                                            selectedActionChip = ChatActionChipData(quizTitle, quizPrompt, Icons.Default.Quiz)
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = stringResource(R.string.chat_action_handout),
                                                color = if (isHandoutAllowed) TextWhite else TextMuted,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            if (!isPro) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(0xFFEAB308).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                        .border(0.5.dp, Color(0xFFEAB308), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = stringResource(R.string.chat_action_pro_badge),
                                                        color = Color(0xFFFDE047),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            } else if (availableTokens < 100) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(0xFFEF4444).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                        .border(0.5.dp, Color(0xFFEF4444), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = stringResource(R.string.chat_action_no_tokens_badge),
                                                        color = Color(0xFFFCA5A5),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = if (isHandoutAllowed) LimeAccent else TextMuted,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                                    enabled = isHandoutAllowed,
                                    onClick = {
                                        showActionMenu = false
                                        if (!isPro) {
                                            Toast.makeText(context, context.getString(R.string.chat_action_pro_required_toast), Toast.LENGTH_SHORT).show()
                                        } else if (availableTokens < 100) {
                                            Toast.makeText(context, context.getString(R.string.chat_action_insufficient_tokens_toast), Toast.LENGTH_SHORT).show()
                                        } else {
                                            selectedActionChip = ChatActionChipData(handoutTitle, handoutPrompt, Icons.Default.AutoAwesome)
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Expanding Input Text
                        BasicTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 12.dp),
                            textStyle = TextStyle(
                                color = TextWhite,
                                fontSize = 16.sp
                            ),
                            maxLines = 4,
                            cursorBrush = SolidColor(LimeAccent),
                            decorationBox = { innerTextField ->
                                if (inputText.isEmpty() && selectedActionChip == null) {
                                    Text(
                                        text = stringResource(R.string.chat_input_hint),
                                        color = TextMuted,
                                        fontSize = 16.sp
                                    )
                                }
                                innerTextField()
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Circular Send Button
                        val hasInput = inputText.trim().isNotBlank() || selectedActionChip != null
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (hasInput) LimeAccent else AppColors.SurfaceVariant2)
                                .bounceClickable(enabled = hasInput) {
                                    val fullMessage = listOfNotNull(selectedActionChip?.prompt, inputText.trim().ifBlank { null }).joinToString(" ")
                                    if (fullMessage.isNotBlank() && !isTyping) {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                        selectedActionChip = null
                                        viewModel.sendMessage(fullMessage)
                                        inputText = ""
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                tint = if (hasInput) AppColors.AccentInk else AppColors.TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
        
        messageToReport?.let { msg ->
            AlertDialog(
                onDismissRequest = { messageToReport = null },
                title = { Text("إبلاغ عن رسالة", color = TextWhite) },
                text = {
                    Column {
                        Text("يرجى اختيار سبب البلاغ:", color = TextMuted)
                        Spacer(modifier = Modifier.height(8.dp))
                        reportReasons.forEach { reason ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedReportReason = reason }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedReportReason == reason,
                                    onClick = { selectedReportReason = reason },
                                    colors = RadioButtonDefaults.colors(selectedColor = LimeAccent)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(reason, color = TextWhite)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (selectedReportReason.isNotBlank()) {
                            viewModel.reportMessage(msg.id, selectedReportReason) { success, error ->
                                if (success) {
                                    Toast.makeText(context, "تم الإبلاغ بنجاح", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, error ?: "حدث خطأ", Toast.LENGTH_SHORT).show()
                                }
                            }
                            messageToReport = null
                            selectedReportReason = ""
                        }
                    }) {
                        Text("إرسال", color = LimeAccent)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { messageToReport = null }) {
                        Text("إلغاء", color = TextMuted)
                    }
                },
                containerColor = CardBG
            )
        }
    }
}

@Composable
fun StyledChatBubble(
    message: ChatMessage,
    onRetry: ((String) -> Unit)? = null,
    onUpgradeClick: (() -> Unit)? = null,
    onReport: ((ChatMessage) -> Unit)? = null
) {
    val isUser = message.isUser
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterStart else Alignment.CenterEnd
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.Start else Alignment.End
        ) {
            if (isUser) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 290.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 18.dp,
                                topEnd = 18.dp,
                                bottomStart = 4.dp,
                                bottomEnd = 18.dp
                            )
                        )
                        .background(LimeAccent)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = message.text,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "10:12",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            } else if (message.isError) {
                // Red/Orange Error Card with Retry or Upgrade Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 24.dp, top = 6.dp, bottom = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF3B1717))
                        .border(1.dp, if (message.isUpgradeRequired) LimeAccent else Color(0xFFEF4444), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = if (message.isUpgradeRequired) {
                                stringResource(R.string.chat_error_upgrade_required)
                            } else {
                                stringResource(R.string.chat_error_generic, message.text.ifBlank { "Error" })
                            },
                            fontSize = 13.sp,
                            color = Color(0xFFFCA5A5),
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (message.isUpgradeRequired) {
                            Button(
                                onClick = { onUpgradeClick?.invoke() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LimeAccent),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.chat_upgrade_button),
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    val failedText = message.failedText
                                    if (!failedText.isNullOrBlank()) {
                                        onRetry?.invoke(failedText)
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.chat_retry_button),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 32.dp, top = 8.dp, bottom = 8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        if (message.text.trim().startsWith("{") && message.text.contains("\"intent\": \"CREATE_TEST\"")) {
                            InteractiveTestView(jsonString = message.text)
                        } else if (message.text.contains(".pdf", ignoreCase = true) || message.text.contains("notebook_", ignoreCase = true)) {
                            PdfHandoutCard(messageText = message.text)
                        } else {
                            ZenoMarkdownText(
                                text = message.text,
                                color = TextWhite
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "10:12",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            val copiedToastMsg = stringResource(R.string.chat_copied_toast)
                            val okBtnMsg = stringResource(R.string.ok_button)

                            Text(
                                text = stringResource(R.string.chat_copy),
                                fontSize = 12.sp,
                                color = TextMuted,
                                modifier = Modifier
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(message.text))
                                        Toast.makeText(context, copiedToastMsg, Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(4.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = stringResource(R.string.chat_report_error),
                                fontSize = 12.sp,
                                color = TextMuted,
                                modifier = Modifier
                                    .clickable {
                                        onReport?.invoke(message)
                                    }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.zeno_typing_indicator),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
        }
    }
}
