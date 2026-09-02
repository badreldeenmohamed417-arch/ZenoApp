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
    val id: String
)

data class SessionActionResponse(
    val success: Boolean,
    val message: String
)
