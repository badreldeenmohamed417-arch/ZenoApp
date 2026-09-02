package com.example.zeno.features.chat.data.dto

import com.google.gson.annotations.SerializedName

data class ChatMessageDTO(
    val id: String,
    val text: String,
    @SerializedName("is_user") val isUser: Boolean,
    @SerializedName("created_at") val createdAt: String
)

data class ChatHistoryResponse(
    val messages: List<ChatMessageDTO>
)

data class ChatSendRequest(
    val message: String
)

data class ChatSendResponse(
    val reply: String
)
