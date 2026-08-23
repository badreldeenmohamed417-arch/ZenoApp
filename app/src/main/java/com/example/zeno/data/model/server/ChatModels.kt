package com.example.zeno.data.model.server

data class CreateConversationRequest(
    val title: String? = null,
    val subject_id: String? = null
)

data class SendMessageRequest(
    val content: String,
    val lesson: String? = null
)

data class MessageResponse(
    val id: String,
    val conversation_id: String,
    val role: String,
    val content: String,
    val created_at: String
)

data class ConversationResponse(
    val id: String,
    val title: String?,
    val subject_id: String?,
    val created_at: String,
    val updated_at: String,
    val last_message_at: String,
    val is_archived: Boolean
)

data class ConversationDetailResponse(
    val id: String,
    val title: String?,
    val subject_id: String?,
    val created_at: String,
    val updated_at: String,
    val last_message_at: String,
    val is_archived: Boolean,
    val messages: List<MessageResponse>
)

data class ConversationListResponse(
    val items: List<ConversationResponse>
)
