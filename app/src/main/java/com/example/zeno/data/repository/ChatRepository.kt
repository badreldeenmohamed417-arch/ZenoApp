package com.example.zeno.data.repository

import com.example.zeno.data.model.server.ConversationDetailResponse
import com.example.zeno.data.model.server.ConversationResponse
import com.example.zeno.data.model.server.CreateConversationRequest
import com.example.zeno.data.model.server.SendMessageRequest
import com.example.zeno.data.server.ApiClient

class ChatRepository {

    private val api
        get() = ApiClient.chat()

    suspend fun createConversation(
        title: String? = null,
        subjectId: String? = null
    ): ConversationResponse {

        return api.createConversation(
            CreateConversationRequest(
                title = title,
                subject_id = subjectId
            )
        )
    }

    suspend fun sendMessage(
        conversationId: String,
        message: String,
        lesson: String? = null
    ): String {

        return api.sendMessage(
            conversationId = conversationId,
            request = SendMessageRequest(
                content = message,
                lesson = lesson
            )
        ).content
    }

    suspend fun getConversation(
        conversationId: String
    ): ConversationDetailResponse {

        return api.getConversation(conversationId)
    }

    suspend fun getConversations(): List<ConversationResponse> {
        return api.getConversations().items
    }

    suspend fun deleteConversation(
        conversationId: String
    ) {
        api.deleteConversation(conversationId)
    }
}
