package com.example.zeno.features.session.data.dto

import com.google.gson.annotations.SerializedName

data class StudySessionDTO(
    val id: String,
    val title: String,
    val subject: String,
    @SerializedName("scheduled_at") val scheduledAt: String,
    val status: String,
    @SerializedName("duration_minutes") val durationMinutes: Int
)

data class SessionListResponse(
    val sessions: List<StudySessionDTO>
)

data class StartSessionRequest(
    @SerializedName("subject_id") val subjectId: String,
    @SerializedName("topic_id") val topicId: String? = "general"
)

data class CompleteStudySessionPayload(
    val minutes: Int = 0,
    @SerializedName("questions_count") val questionsCount: Int = 0
)

data class SessionActionResponse(
    val sessionId: String?,
    val subjectId: String?,
    val status: String?,
    val minutes: Int?,
    @SerializedName("questions_count") val questionsCount: Int?
)
