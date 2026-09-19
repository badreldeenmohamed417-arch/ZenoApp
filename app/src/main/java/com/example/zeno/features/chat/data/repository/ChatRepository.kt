package com.example.zeno.features.chat.data.repository

import com.example.zeno.features.chat.data.ChatApi
import com.example.zeno.features.chat.data.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

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

    suspend fun uploadFile(file: MultipartBody.Part): Result<OcrResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.uploadFile(file)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getConversations(): Result<ConversationListResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.getConversations()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getConversationDetails(id: String): Result<ConversationDetailResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.getConversation(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createConversation(title: String?, subjectId: String? = null): Result<ConversationResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.createConversation(CreateConversationRequest(title = title, subjectId = subjectId))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendConversationMessage(id: String, content: String): Result<MessageResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.sendConversationMessage(id, SendMessageRequest(content = content))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateConversationTitle(id: String, title: String): Result<ConversationResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.updateConversationTitle(id, UpdateConversationRequest(title = title))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteConversation(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            chatApi.deleteConversation(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun reportMessage(id: String, reason: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            chatApi.reportMessage(id, ReportRequest(reason = reason))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
