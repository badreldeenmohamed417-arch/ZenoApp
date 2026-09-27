package com.example.zeno.features.chat.data

import com.example.zeno.features.chat.data.dto.*
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Multipart
import retrofit2.http.Part

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
