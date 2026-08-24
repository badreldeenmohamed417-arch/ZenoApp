package com.example.zeno.data.repository

import com.example.zeno.data.local.db.ChatDao
import com.example.zeno.data.local.db.ConversationEntity
import com.example.zeno.data.local.db.MessageEntity
import com.example.zeno.data.model.server.ConversationDetailResponse
import com.example.zeno.data.model.server.ConversationResponse
import com.example.zeno.data.model.server.CreateConversationRequest
import com.example.zeno.data.model.server.MessageResponse
import com.example.zeno.data.model.server.SendMessageRequest
import com.example.zeno.data.server.ApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository(private val chatDao: ChatDao) {

    private val api
        get() = ApiClient.chat()

    suspend fun createConversation(
        title: String? = null,
        subjectId: String? = null
    ): ConversationResponse {
        val response = api.createConversation(
            CreateConversationRequest(
                title = title,
                subjectId = subjectId
            )
        )
        // Save to local
        chatDao.insertConversations(listOf(
            ConversationEntity(
                id = response.id,
                title = response.title,
                subjectId = response.subjectId,
                updatedAt = response.updatedAt,
                lastMessageAt = response.lastMessageAt
            )
        ))
        return response
    }

    suspend fun sendMessage(
        conversationId: String,
        message: String,
        lesson: String? = null
    ): MessageResponse {
        // 1. Save user message to local first
        chatDao.insertMessage(
            MessageEntity(
                id = java.util.UUID.randomUUID().toString(),
                conversationId = conversationId,
                role = "user",
                content = message,
                createdAt = java.util.Date().toString() // Simple timestamp
            )
        )

        // 2. Send to API
        val response = api.sendMessage(
            conversationId = conversationId,
            request = SendMessageRequest(
                content = message,
                lesson = lesson
            )
        )
        // 3. Save assistant response to local
        chatDao.insertMessage(
            MessageEntity(
                id = response.id,
                conversationId = response.conversationId,
                role = response.role,
                content = response.content,
                createdAt = response.createdAt
            )
        )
        return response
    }

    suspend fun getConversation(
        conversationId: String
    ): ConversationDetailResponse {
        val response = api.getConversation(conversationId)
        // Sync local
        chatDao.insertMessages(response.messages.map {
            MessageEntity(
                id = it.id,
                conversationId = it.conversationId,
                role = it.role,
                content = it.content,
                createdAt = it.createdAt
            )
        })
        return response
    }

    fun getLocalMessages(conversationId: String): Flow<List<MessageResponse>> {
        return chatDao.getMessages(conversationId).map { entities ->
            entities.map {
                MessageResponse(
                    id = it.id,
                    conversationId = it.conversationId,
                    role = it.role,
                    content = it.content,
                    createdAt = it.createdAt
                )
            }
        }
    }

    suspend fun getConversations(): List<ConversationResponse> {
        val response = api.getConversations().items
        chatDao.insertConversations(response.map {
            ConversationEntity(
                id = it.id,
                title = it.title,
                subjectId = it.subjectId,
                updatedAt = it.updatedAt,
                lastMessageAt = it.lastMessageAt
            )
        })
        return response
    }

    fun getLocalConversations(): Flow<List<ConversationResponse>> {
        return chatDao.getConversations().map { entities ->
            entities.map {
                ConversationResponse(
                    id = it.id,
                    title = it.title,
                    subjectId = it.subjectId,
                    createdAt = "", // Not in entity for simplicity
                    updatedAt = it.updatedAt,
                    lastMessageAt = it.lastMessageAt,
                    isArchived = false
                )
            }
        }
    }

    suspend fun deleteConversation(
        conversationId: String
    ) {
        api.deleteConversation(conversationId)
        // Should also delete from local
    }

    suspend fun clearAllLocalData() {
        chatDao.clearConversations()
        chatDao.clearMessages()
    }
}
