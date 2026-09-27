package com.example.zeno.features.chat.data.repository

import com.example.zeno.data.local.db.ChatDao
import com.example.zeno.data.local.db.ConversationEntity
import com.example.zeno.data.local.db.MessageEntity
import com.example.zeno.features.chat.data.ChatApi
import com.example.zeno.features.chat.data.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class ChatRepository(
    private val chatApi: ChatApi,
    private val chatDao: ChatDao
) {
    fun observeLocalConversations(): Flow<List<ConversationEntity>> = chatDao.getConversations()

    fun observeLocalMessages(conversationId: String): Flow<List<MessageEntity>> =
        chatDao.getMessages(conversationId)

    suspend fun syncConversations(): Result<ConversationListResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.getConversations()
            val remoteIds = response.items.map { it.id }.toSet()
            chatDao.getConversationIds().filterNot { it in remoteIds }.forEach {
                chatDao.deleteMessages(it)
                chatDao.deleteConversation(it)
            }
            chatDao.insertConversations(response.items.map {
                ConversationEntity(
                    id = it.id,
                    title = it.title,
                    subjectId = it.subjectId,
                    updatedAt = it.updatedAt ?: "",
                    lastMessageAt = it.lastMessageAt ?: ""
                )
            })
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getConversations(): Result<ConversationListResponse> = syncConversations()

    suspend fun getChatHistory(): Result<ChatHistoryResponse> = withContext(Dispatchers.IO) {
        try { Result.success(chatApi.getChatHistory()) } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getConversationDetails(id: String): Result<ConversationDetailResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.getConversation(id)
            chatDao.deleteMessages(id)
            chatDao.insertMessages(response.messages.map {
                MessageEntity(it.id, id, it.role, it.content, it.createdAt ?: "")
            })
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(message: String): Result<ChatSendResponse> = withContext(Dispatchers.IO) {
        try { Result.success(chatApi.sendMessage(ChatSendRequest(message = message))) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun uploadFile(file: MultipartBody.Part): Result<OcrResponse> = withContext(Dispatchers.IO) {
        try { Result.success(chatApi.uploadFile(file)) } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun createConversation(title: String?, subjectId: String? = null): Result<ConversationResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.createConversation(CreateConversationRequest(title = title, subjectId = subjectId))
            chatDao.insertConversations(listOf(ConversationEntity(
                response.id, response.title, response.subjectId,
                response.updatedAt ?: "", response.lastMessageAt ?: ""
            )))
            Result.success(response)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun sendConversationMessage(id: String, content: String): Result<MessageResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.sendConversationMessage(id, SendMessageRequest(content = content))
            chatDao.insertMessage(MessageEntity(response.id, id, response.role, response.content, response.createdAt ?: ""))
            return@withContext Result.success(response)
        } catch (e: Exception) {
            // The server removes failed user messages and running AI requests transactionally.
            chatDao.deleteMessageByConversationAndContent(id, content)
            Result.failure(e)
        }
    }

    suspend fun cacheUserMessage(id: String, messageId: String, content: String) = withContext(Dispatchers.IO) {
        chatDao.insertMessage(MessageEntity(messageId, id, "user", content, System.currentTimeMillis().toString()))
    }

    suspend fun deleteConversation(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            chatApi.deleteConversation(id)
            chatDao.deleteMessages(id)
            chatDao.deleteConversation(id)
            Result.success(Unit)
        } catch (e: Exception) {
            // Treat already-deleted server conversations as success and keep local DB in sync.
            val message = e.message.orEmpty()
            if (message.contains("404") || message.contains("not found", true)) {
                chatDao.deleteMessages(id)
                chatDao.deleteConversation(id)
                Result.success(Unit)
            } else Result.failure(e)
        }
    }

    suspend fun updateConversationTitle(id: String, title: String): Result<ConversationResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.updateConversationTitle(id, UpdateConversationRequest(title = title))
            chatDao.insertConversations(listOf(ConversationEntity(
                response.id, response.title, response.subjectId,
                response.updatedAt ?: "", response.lastMessageAt ?: ""
            )))
            Result.success(response)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun reportMessage(id: String, reason: String): Result<Unit> = withContext(Dispatchers.IO) {
        try { chatApi.reportMessage(id, ReportRequest(reason = reason)); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }
}
