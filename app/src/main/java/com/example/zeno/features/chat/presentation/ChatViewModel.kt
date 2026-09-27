package com.example.zeno.features.chat.presentation

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.zeno.R
import com.example.zeno.core.base.BaseViewModel
import com.example.zeno.data.local.UserManager
import com.example.zeno.features.chat.data.dto.ConversationResponse
import com.example.zeno.features.chat.data.repository.ChatRepository
import com.example.zeno.features.chat.domain.ChatMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.WebSocket
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
    private val _progressStage = MutableStateFlow("thinking")
    val progressStage: StateFlow<String> = _progressStage.asStateFlow()
    private var currentWebSocket: WebSocket? = null
    private var currentJob: Job? = null

    init { userManager.saveCurrentChatId(null); fetchConversations() }

    fun fetchConversations() { viewModelScope.launch { repository.getConversations().onSuccess { _conversations.value = it.items } } }

    fun clearChat() { stopGeneration(); _messages.value = emptyList(); _errorMessage.value = null; _activeTitle.value = ""; userManager.saveCurrentChatId(null) }

    fun stopGeneration() {
        currentWebSocket?.close(1000, "user_stop")
        currentWebSocket = null
        currentJob?.cancel()
        currentJob = null
        val pending = _messages.value.lastOrNull { it.isUser }
        if (pending != null) _messages.value = _messages.value.filterNot { it.id == pending.id }
        _isTyping.value = false
    }

    fun loadConversation(id: String) {
        userManager.saveCurrentChatId(id)
        _activeTitle.value = _conversations.value.firstOrNull { it.id == id }?.title ?: ""
        _isLoadingChat.value = true
        viewModelScope.launch {
            val result = repository.getConversationDetails(id)
            _isLoadingChat.value = false
            if (result.isSuccess) _messages.value = result.getOrNull()?.messages?.map { ChatMessage(it.id, it.content, it.role == "user", System.currentTimeMillis()) } ?: emptyList()
            else _errorMessage.value = getApplication<Application>().getString(R.string.chat_error_load_history)
        }
    }

    fun deleteConversation(id: String) { viewModelScope.launch { repository.deleteConversation(id); if (userManager.getCurrentChatId() == id) clearChat(); fetchConversations() } }
    fun archiveConversation(id: String) = deleteConversation(id)
    fun renameConversation(id: String, title: String) { viewModelScope.launch { if (repository.updateConversationTitle(id, title).isSuccess) { if (userManager.getCurrentChatId() == id) _activeTitle.value = title; fetchConversations() } } }

    fun sendMessage(text: String, hiddenPrefix: String? = null) {
        if (text.isBlank() || _isTyping.value) return
        val userMessage = ChatMessage(UUID.randomUUID().toString(), text, true, System.currentTimeMillis())
        _messages.value = _messages.value + userMessage
        _isTyping.value = true
        _errorMessage.value = null
        _progressStage.value = "thinking"
        currentJob = viewModelScope.launch {
            try {
                var id = userManager.getCurrentChatId()
                if (id == null) {
                    val created = repository.createConversation(text.take(40))
                    id = created.getOrNull()?.id
                    if (id != null) { userManager.saveCurrentChatId(id); _activeTitle.value = text.take(40); fetchConversations() }
                }
                if (id == null) throw IllegalStateException(getApplication<Application>().getString(R.string.chat_error_create_conversation))
                val conversationId = id
                val fullText = hiddenPrefix?.let { it + " " + text } ?: text
                currentWebSocket = repository.openWebSocket(conversationId, fullText,
                    onProgress = { _progressStage.value = it },
                    onStarted = { userManager.saveCurrentChatId(it) },
                    onCompleted = { reply -> viewModelScope.launch {
                        val details = repository.getConversationDetails(conversationId)
                        _messages.value = details.getOrNull()?.messages?.map { dto -> ChatMessage(dto.id, dto.content, dto.role == "user", System.currentTimeMillis()) } ?: (_messages.value + ChatMessage(reply.id, reply.content, false, System.currentTimeMillis()))
                        _isTyping.value = false
                        currentWebSocket = null
                    } },
                    onError = { error -> viewModelScope.launch {
                        _messages.value = _messages.value.filterNot { it.id == userMessage.id }
                        _isTyping.value = false
                        currentWebSocket = null
                        _errorMessage.value = error.message ?: getApplication<Application>().getString(R.string.chat_error_fallback)
                    } }
                )
            } catch (e: Exception) {
                _messages.value = _messages.value.filterNot { it.id == userMessage.id }
                _isTyping.value = false
                _errorMessage.value = e.message ?: getApplication<Application>().getString(R.string.chat_error_fallback)
            }
        }
    }

    fun retryMessage(text: String) { sendMessage(text) }

    fun uploadFile(uri: Uri, resolver: ContentResolver, onTextExtracted: (String) -> Unit) {
        _isTyping.value = true
        viewModelScope.launch {
            try {
                resolver.openInputStream(uri)?.use { stream ->
                    val body = RequestBody.create("image/*".toMediaTypeOrNull(), stream.readBytes())
                    val result = repository.uploadFile(MultipartBody.Part.createFormData("file", "upload.jpg", body))
                    if (result.isSuccess) onTextExtracted(result.getOrNull()?.text.orEmpty()) else _errorMessage.value = getApplication<Application>().getString(R.string.chat_error_upload_file, result.exceptionOrNull()?.message.orEmpty())
                }
            } catch (e: Exception) { _errorMessage.value = getApplication<Application>().getString(R.string.chat_error_process_file) }
            finally { _isTyping.value = false }
        }
    }

    fun feedbackMessage(id: String, feedback: String) { viewModelScope.launch { repository.feedbackMessage(id, feedback) } }
    fun reportMessage(id: String, reason: String, onResult: (Boolean, String?) -> Unit) { viewModelScope.launch { val r = repository.reportMessage(id, reason); onResult(r.isSuccess, r.exceptionOrNull()?.message) } }
}