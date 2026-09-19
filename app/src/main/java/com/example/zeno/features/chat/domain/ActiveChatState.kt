package com.example.zeno.features.chat.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ActiveChatState {
    private val _conversationId = MutableStateFlow<String?>(null)
    val conversationId = _conversationId.asStateFlow()

    private val _conversationTitle = MutableStateFlow<String?>("")
    val conversationTitle = _conversationTitle.asStateFlow()

    fun setChat(id: String?, title: String?) {
        _conversationId.value = id
        _conversationTitle.value = title ?: ""
    }

    fun updateTitle(title: String) {
        _conversationTitle.value = title
    }

    fun clear() {
        _conversationId.value = null
        _conversationTitle.value = ""
    }
}
