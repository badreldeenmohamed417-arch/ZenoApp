package com.example.zeno.features.chat.data

import com.example.zeno.features.chat.data.dto.ChatHistoryResponse
import com.example.zeno.features.chat.data.dto.ChatSendRequest
import com.example.zeno.features.chat.data.dto.ChatSendResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ChatApi {
    @GET("main/chat/history")
    suspend fun getChatHistory(): ChatHistoryResponse

    @POST("main/chat/send")
    suspend fun sendMessage(@Body request: ChatSendRequest): ChatSendResponse
}
