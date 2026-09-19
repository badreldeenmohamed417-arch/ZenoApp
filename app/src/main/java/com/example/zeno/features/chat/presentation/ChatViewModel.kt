package com.example.zeno.features.chat.presentation

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeno.data.local.UserManager
import com.example.zeno.features.chat.data.dto.ConversationResponse
import com.example.zeno.features.chat.data.repository.ChatRepository
import com.example.zeno.features.chat.domain.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.UUID

class ChatViewModel(private val repository: ChatRepository, private val userManager: UserManager) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _isLoadingChat = MutableStateFlow(false)
    val isLoadingChat: StateFlow<Boolean> = _isLoadingChat.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _conversations = MutableStateFlow<List<ConversationResponse>>(emptyList())
    val conversations: StateFlow<List<ConversationResponse>> = _conversations.asStateFlow()

    private val _activeTitle = MutableStateFlow("")
    val activeTitle: StateFlow<String> = _activeTitle.asStateFlow()

    init {
        fetchConversations()
        loadHistoryIfNeeded()
    }

    fun fetchConversations() {
        viewModelScope.launch {
            val result = repository.getConversations()
            if (result.isSuccess) {
                _conversations.value = result.getOrNull()?.items ?: emptyList()
                val currentId = userManager.getCurrentChatId()
                if (currentId != null) {
                    val conv = _conversations.value.find { it.id == currentId }
                    _activeTitle.value = conv?.title ?: ""
                }
            }
        }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            val result = repository.deleteConversation(id)
            if (result.isSuccess) {
                if (userManager.getCurrentChatId() == id) {
                    clearChat()
                }
                fetchConversations()
            }
        }
    }

    fun archiveConversation(id: String) {
        deleteConversation(id)
    }

    fun loadConversation(id: String) {
        userManager.saveCurrentChatId(id)
        val conv = _conversations.value.find { it.id == id }
        _activeTitle.value = conv?.title ?: ""
        _messages.value = emptyList()
        _isLoadingChat.value = true

        viewModelScope.launch {
            val result = repository.getConversationDetails(id)
            _isLoadingChat.value = false
            if (result.isSuccess) {
                val history = result.getOrNull()?.messages?.map { dto ->
                    ChatMessage(
                        id = dto.id,
                        text = dto.content,
                        isUser = dto.role == "user",
                        timestamp = System.currentTimeMillis()
                    )
                } ?: emptyList()
                _messages.value = history
            } else {
                _errorMessage.value = "Failed to load chat history"
            }
        }
    }

    fun renameConversation(id: String, newTitle: String) {
        viewModelScope.launch {
            val result = repository.updateConversationTitle(id, newTitle)
            if (result.isSuccess) {
                if (userManager.getCurrentChatId() == id) {
                    _activeTitle.value = newTitle
                }
                fetchConversations()
            }
        }
    }

    private fun loadHistoryIfNeeded() {
        val currentId = userManager.getCurrentChatId()
        if (currentId != null) {
            _isLoadingChat.value = true
            viewModelScope.launch {
                val result = repository.getConversationDetails(currentId)
                _isLoadingChat.value = false
                if (result.isSuccess) {
                    val history = result.getOrNull()?.messages?.map { dto ->
                        ChatMessage(
                            id = dto.id,
                            text = dto.content,
                            isUser = dto.role == "user",
                            timestamp = System.currentTimeMillis()
                        )
                    } ?: emptyList()
                    _messages.value = history
                } else {
                    _errorMessage.value = "Failed to load chat history"
                }
            }
        }
    }

    fun clearChat() {
        _messages.value = emptyList()
        _errorMessage.value = null
        _activeTitle.value = ""
        _isLoadingChat.value = false
        userManager.saveCurrentChatId(null)
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = text,
            isUser = true,
            timestamp = System.currentTimeMillis()
        )

        _messages.value = _messages.value + userMessage
        _isTyping.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            var activeId = userManager.getCurrentChatId()
            if (activeId == null) {
                val title = if (text.length > 20) text.take(20) + "..." else text
                val result = repository.createConversation(title)
                if (result.isSuccess) {
                    activeId = result.getOrNull()?.id
                    if (activeId != null) {
                        userManager.saveCurrentChatId(activeId)
                        _activeTitle.value = title
                        fetchConversations()
                    }
                }
            }

            if (activeId == null) {
                _isTyping.value = false
                val errorBotMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = "حدث خطأ أثناء إنشاء المحادثة.",
                    isUser = false,
                    timestamp = System.currentTimeMillis(),
                    isError = true
                )
                _messages.value = _messages.value + errorBotMessage
                return@launch
            }

            val result = repository.sendConversationMessage(activeId, text)
            _isTyping.value = false

            if (result.isSuccess) {
                val replyDto = result.getOrNull()
                val replyText = replyDto?.content ?: ""
                val botMessage = ChatMessage(
                    id = replyDto?.id ?: UUID.randomUUID().toString(),
                    text = replyText,
                    isUser = false,
                    timestamp = System.currentTimeMillis()
                )
                _messages.value = _messages.value + botMessage
            } else {
                val exception = result.exceptionOrNull()
                val errReason = exception?.message ?: ""
                val isUpgrade = errReason.contains("402") || errReason.contains("502") || errReason.contains("Payment", ignoreCase = true) || errReason.contains("Limit", ignoreCase = true) || errReason.contains("Upgrade", ignoreCase = true)

                val errorBotMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = errReason,
                    isUser = false,
                    timestamp = System.currentTimeMillis(),
                    isError = true,
                    isUpgradeRequired = isUpgrade,
                    failedText = text
                )
                _messages.value = _messages.value + errorBotMessage
            }
        }
    }

    fun retryMessage(failedText: String) {
        _messages.value = _messages.value.filterNot { it.isError && it.failedText == failedText }
        sendMessage(failedText)
    }

    fun editMessage(messageId: String, onTextLoaded: (String) -> Unit) {
        val index = _messages.value.indexOfFirst { it.id == messageId }
        if (index != -1) {
            val msg = _messages.value[index]
            if (msg.isUser) {
                val updatedList = _messages.value.toMutableList()
                updatedList.removeAt(index)
                if (index < updatedList.size && !updatedList[index].isUser) {
                    updatedList.removeAt(index)
                }
                _messages.value = updatedList
                onTextLoaded(msg.text)
            }
        }
    }

    fun uploadFile(uri: Uri, contentResolver: ContentResolver, onTextExtracted: (String) -> Unit) {
        _isTyping.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val bytes = inputStream.readBytes()
                    val mediaType = "image/*".toMediaTypeOrNull()
                    val requestBody = RequestBody.create(mediaType, bytes)
                    val part = MultipartBody.Part.createFormData("file", "upload.jpg", requestBody)

                    val result = repository.uploadFile(part)
                    _isTyping.value = false

                    if (result.isSuccess) {
                        val text = result.getOrNull()?.text ?: ""
                        onTextExtracted(text)
                    } else {
                        val errText = result.exceptionOrNull()?.message ?: "auto_str_فشل_معالجة_الملف"
                        val errorBotMessage = ChatMessage(
                            id = UUID.randomUUID().toString(),
                            text = "حدث خطأ أثناء رفع المستند: $errText",
                            isUser = false,
                            timestamp = System.currentTimeMillis(),
                            isError = true
                        )
                        _messages.value = _messages.value + errorBotMessage
                    }
                } else {
                    _isTyping.value = false
                    val errorBotMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        text = "auto_str_عذرا_تعذر_قراءة",
                        isUser = false,
                        timestamp = System.currentTimeMillis(),
                        isError = true
                    )
                    _messages.value = _messages.value + errorBotMessage
                }
            } catch (e: Exception) {
                _isTyping.value = false
                val errorBotMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = "حدث خطأ أثناء قراءة المستند: ${e.message}",
                    isUser = false,
                    timestamp = System.currentTimeMillis(),
                    isError = true
                )
                _messages.value = _messages.value + errorBotMessage
            }
        }
    }

    fun reportMessage(id: String, reason: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.reportMessage(id, reason)
            if (result.isSuccess) {
                onResult(true, null)
            } else {
                val errorMsg = result.exceptionOrNull()?.let { com.example.zeno.core.NetworkUtils.getErrorMessage(it) } ?: "Failed to report message"
                onResult(false, errorMsg)
            }
        }
    }
}
