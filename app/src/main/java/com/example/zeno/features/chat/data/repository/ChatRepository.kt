package com.example.zeno.features.chat.data.repository

import com.example.zeno.features.chat.data.ChatApi
import com.example.zeno.features.chat.data.dto.ChatHistoryResponse
import com.example.zeno.features.chat.data.dto.ChatSendRequest
import com.example.zeno.features.chat.data.dto.ChatSendResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatRepository(private val chatApi: ChatApi) {
    suspend fun getChatHistory(): Result<ChatHistoryResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.getChatHistory()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(message: String): Result<ChatSendResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.sendMessage(ChatSendRequest(message = message))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
