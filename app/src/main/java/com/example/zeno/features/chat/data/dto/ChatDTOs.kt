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

data class CreateConversationRequest(
    val title: String?,
    @SerializedName("subject_id") val subjectId: String? = null
)

data class UpdateConversationRequest(
    val title: String
)

data class SendMessageRequest(
    val content: String,
    val lesson: String? = null
)

data class MessageResponse(
    val id: String,
    @SerializedName("conversation_id") val conversationId: String,
    val role: String,
    val content: String,
    @SerializedName("created_at") val createdAt: String
)

open class ConversationResponse(
    val id: String,
    val title: String?,
    @SerializedName("subject_id") val subjectId: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("last_message_at") val lastMessageAt: String,
    @SerializedName("is_archived") val isArchived: Boolean
)

class ConversationDetailResponse(
    id: String,
    title: String?,
    subjectId: String?,
    createdAt: String,
    updatedAt: String,
    lastMessageAt: String,
    isArchived: Boolean,
    val messages: List<MessageResponse>
) : ConversationResponse(id, title, subjectId, createdAt, updatedAt, lastMessageAt, isArchived)

data class ConversationListResponse(
    val items: List<ConversationResponse>
)

data class ReportRequest(
    val reason: String
)
