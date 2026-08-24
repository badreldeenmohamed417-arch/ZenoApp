package com.example.zeno.data.serverConnections

import com.example.zeno.data.model.server.ConversationDetailResponse
import com.example.zeno.data.model.server.ConversationListResponse
import com.example.zeno.data.model.server.ConversationResponse
import com.example.zeno.data.model.server.CreateConversationRequest
import com.example.zeno.data.model.server.MessageResponse
import com.example.zeno.data.model.server.SendMessageRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApi {

    @POST("main/chat/conversations")
    suspend fun createConversation(
        @Body request: CreateConversationRequest
    ): ConversationResponse

    @GET("main/chat/conversations")
    suspend fun getConversations(): ConversationListResponse

    @GET("main/chat/conversations/{conversationId}")
    suspend fun getConversation(
        @Path("conversationId") conversationId: String
    ): ConversationDetailResponse

    @POST("main/chat/conversations/{conversationId}/messages")
    suspend fun sendMessage(
        @Path("conversationId") conversationId: String,
        @Body request: SendMessageRequest
    ): MessageResponse

    @DELETE("main/chat/conversations/{conversationId}")
    suspend fun deleteConversation(
        @Path("conversationId") conversationId: String
    )
}
