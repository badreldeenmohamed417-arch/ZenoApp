package com.example.zeno.features.chat.presentation

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import com.example.zeno.core.base.BaseViewModel
import com.example.zeno.R
import androidx.lifecycle.viewModelScope
import com.example.zeno.data.local.UserManager
import com.example.zeno.features.chat.data.dto.ConversationResponse
import com.example.zeno.features.chat.data.repository.ChatRepository
import com.example.zeno.features.chat.domain.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.UUID

class ChatViewModel(application: Application, private val repository: ChatRepository, private val userManager: UserManager) : BaseViewModel(application) {
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

    private var currentChatJob: Job? = null
    private var currentWebSocket: okhttp3.WebSocket? = null

    private val _progressStage = MutableStateFlow("thinking")
    val progressStage: StateFlow<String> = _progressStage.asStateFlow()

    init {
        userManager.saveCurrentChatId(null)
        fetchConversations()
    }

    fun fetchConversations() {
        viewModelScope.launch(exceptionHandler) {
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
        _conversations.value = _conversations.value.filter { it.id != id }
        if (userManager.getCurrentChatId() == id) {
            clearChat()
        }
        viewModelScope.launch(exceptionHandler) {
            val result = repository.deleteConversation(id)
            fetchConversations()
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

        viewModelScope.launch(exceptionHandler) {
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
                _errorMessage.value = getApplication<Application>().getString(R.string.chat_error_load_history)
            }
        }
    }

    fun renameConversation(id: String, newTitle: String) {
        viewModelScope.launch(exceptionHandler) {
            val result = repository.updateConversationTitle(id, newTitle)
            if (result.isSuccess) {
                if (userManager.getCurrentChatId() == id) {
                    _activeTitle.value = newTitle
                }
                fetchConversations()
            }
        }
    }

    
    fun stopGeneration() {
        currentWebSocket?.close(1000, "user_stop")
        currentWebSocket = null
        currentChatJob?.cancel()
        currentChatJob = null
        _isTyping.value = false
        val pendingUser = _messages.value.lastOrNull { it.isUser }
        if (pendingUser != null) _messages.value = _messages.value.filterNot { it.id == pendingUser.id }
    }

    fun clearChat() {
        _messages.value = emptyList()
        _errorMessage.value = null
        _activeTitle.value = ""
        _isLoadingChat.value = false
        userManager.saveCurrentChatId(null)
    }

    fun sendMessage(text: String, hiddenPrefix: String? = null) {
        if (text.isBlank() || _isTyping.value) return
        val userMessage = ChatMessage(UUID.randomUUID().toString(), text, true, System.currentTimeMillis())
        _messages.value = _messages.value + userMessage
        _isTyping.value = true
        _errorMessage.value = null
        _progressStage.value = "thinking"
        currentChatJob = viewModelScope.launch(exceptionHandler) {
            try {
                var activeId = userManager.getCurrentChatId()
                if (activeId == null) {
                    val title = text.take(40)
                    val result = repository.createConversation(title)
                    if (result.isSuccess) {
                        activeId = result.getOrNull()?.id
                        if (activeId != null) { userManager.saveCurrentChatId(activeId); _activeTitle.value = title; fetchConversations() }
                    }
                }
                if (activeId == null) throw IllegalStateException(getApplication<Application>().getString(R.string.chat_error_create_conversation))
                val fullText = hiddenPrefix?.let { it + " " + text } ?: text
                val conversationId = activeId
                currentWebSocket = repository.openWebSocket(conversationId, fullText,
                    onProgress = { stage -> _progressStage.value = stage },
                    onStarted = { id -> userManager.saveCurrentChatId(id) },
                    onCompleted = { reply ->
                        viewModelScope.launch {
                            val details = repository.getConversationDetails(conversationId)
                            if (details.isSuccess) {
                                _messages.value = details.getOrNull()?.messages?.map { dto -> ChatMessage(dto.id, dto.content, dto.role == "user", System.currentTimeMillis()) } ?: emptyList()
                            } else { _messages.value = _messages.value + ChatMessage(reply.id, reply.content, false, System.currentTimeMillis()) }
                            _isTyping.value = false
                            currentWebSocket = null
                        }
                    },
                    onError = { error ->
                        viewModelScope.launch {
                            _messages.value = _messages.value.filterNot { it.id == userMessage.id }
                            _isTyping.value = false
                            currentWebSocket = null
                            _errorMessage.value = error.message ?: getApplication<Application>().getString(R.string.chat_error_fallback)
                        }
                    }
                )
            } catch (e: Exception) {
                _messages.value = _messages.value.filterNot { it.id == userMessage.id }
                _isTyping.value = false
                _errorMessage.value = e.message ?: getApplication<Application>().getString(R.string.chat_error_fallback)
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

        viewModelScope.launch(exceptionHandler) {
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
                        val errText = result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.chat_error_process_file)
                        val errorBotMessage = ChatMessage(
                            id = UUID.randomUUID().toString(),
                            text = getApplication<Application>().getString(R.string.chat_error_upload_file, errText),
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
                        text = getApplication<Application>().getString(R.string.chat_error_read_file),
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
                    text = getApplication<Application>().getString(R.string.chat_error_read_file),
                    isUser = false,
                    timestamp = System.currentTimeMillis(),
                    isError = true
                )
                _messages.value = _messages.value + errorBotMessage
            }
        }
    }

    fun feedbackMessage(messageId: String, feedback: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch(exceptionHandler) {
            val result = repository.feedbackMessage(messageId, feedback)
            onResult(result.isSuccess)
        }
    }

    fun reportMessage(id: String, reason: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            val result = repository.reportMessage(id, reason)
            if (result.isSuccess) {
                onResult(true, null)
            } else {
                val errorMsg = result.exceptionOrNull()?.let { com.example.zeno.core.NetworkUtils.getErrorMessage(it) } ?: getApplication<Application>().getString(R.string.chat_error_report_message)
                onResult(false, errorMsg)
            }
        }
    }
}
