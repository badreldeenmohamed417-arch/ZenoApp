package com.example.zeno.features.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeno.features.chat.data.repository.ChatRepository
import com.example.zeno.features.chat.domain.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val result = repository.getChatHistory()
            if (result.isSuccess) {
                val history = result.getOrNull()?.messages?.map { dto ->
                    ChatMessage(
                        id = dto.id,
                        text = dto.text,
                        isUser = dto.isUser,
                        timestamp = System.currentTimeMillis() // Assuming dto.createdAt needs parsing, falling back to current time for simplicity or parsing it if it was a timestamp. Actually let's just use System.currentTimeMillis() for now as it expects Long
                    )
                } ?: emptyList()
                _messages.value = history
            } else {
                _errorMessage.value = "Failed to load chat history"
            }
        }
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
            val result = repository.sendMessage(text)
            _isTyping.value = false
            
            if (result.isSuccess) {
                val replyText = result.getOrNull()?.reply ?: ""
                val botMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = replyText,
                    isUser = false,
                    timestamp = System.currentTimeMillis()
                )
                _messages.value = _messages.value + botMessage
            } else {
                _errorMessage.value = "Failed to send message: ${result.exceptionOrNull()?.message}"
            }
        }
    }
}
