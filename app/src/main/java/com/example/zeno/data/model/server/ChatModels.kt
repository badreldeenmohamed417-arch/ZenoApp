package com.example.zeno.data.model.server

import com.google.gson.annotations.SerializedName

data class CreateConversationRequest(
    val title: String? = null,
    @SerializedName("subject_id") val subjectId: String? = null
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

data class ConversationResponse(
    val id: String,
    val title: String?,
    @SerializedName("subject_id") val subjectId: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("last_message_at") val lastMessageAt: String,
    @SerializedName("is_archived") val isArchived: Boolean
)

data class ConversationDetailResponse(
    val id: String,
    val title: String?,
    @SerializedName("subject_id") val subjectId: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("last_message_at") val lastMessageAt: String,
    @SerializedName("is_archived") val isArchived: Boolean,
    val messages: List<MessageResponse>
)

data class ConversationListResponse(
    val items: List<ConversationResponse>
)
