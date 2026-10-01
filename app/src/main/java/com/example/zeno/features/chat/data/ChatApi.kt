package com.example.zeno.features.chat.data

import com.example.zeno.features.chat.data.dto.ChatHistoryResponse
import com.example.zeno.features.chat.data.dto.ChatSendRequest
import com.example.zeno.features.chat.data.dto.ChatSendResponse
import com.example.zeno.features.chat.data.dto.ConversationDetailResponse
import com.example.zeno.features.chat.data.dto.ConversationListResponse
import com.example.zeno.features.chat.data.dto.ConversationResponse
import com.example.zeno.features.chat.data.dto.CreateConversationRequest
import com.example.zeno.features.chat.data.dto.MessageResponse
import com.example.zeno.features.chat.data.dto.OcrResponse
import com.example.zeno.features.chat.data.dto.ReportRequest
import com.example.zeno.features.chat.data.dto.SendMessageRequest
import com.example.zeno.features.chat.data.dto.UpdateConversationRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ChatApi {
    @GET("main/chat/history")
    suspend fun getChatHistory(): ChatHistoryResponse

    @POST("main/chat/send")
    suspend fun sendMessage(@Body request: ChatSendRequest): ChatSendResponse
    
    @Multipart
    @POST("main/chat/upload")
    suspend fun uploadFile(@Part file: okhttp3.MultipartBody.Part): OcrResponse
    
    @GET("main/chat/conversations")
    suspend fun getConversations(): ConversationListResponse

    @GET("main/chat/conversations/{id}")
    suspend fun getConversation(@Path("id") id: String): ConversationDetailResponse

    @POST("main/chat/conversations")
    suspend fun createConversation(@Body request: CreateConversationRequest): ConversationResponse

    @POST("main/chat/conversations/{id}/messages")
    suspend fun sendConversationMessage(
        @Path("id") id: String, 
        @Body request: SendMessageRequest
    ): MessageResponse

    @DELETE("main/chat/conversations/{id}")
    suspend fun deleteConversation(@Path("id") id: String)

    @retrofit2.http.PATCH("main/chat/conversations/{id}")
    suspend fun updateConversationTitle(
        @Path("id") id: String,
        @Body request: UpdateConversationRequest
    ): ConversationResponse

    @POST("main/chat/messages/{id}/report")
    suspend fun reportMessage(
        @Path("id") id: String,
        @Body request: ReportRequest
    )
}
