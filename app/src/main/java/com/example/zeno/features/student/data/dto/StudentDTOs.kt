package com.example.zeno.features.student.data.dto

import com.google.gson.annotations.SerializedName

data class StudentDashboardResponse(
    val student: StudentOverviewDTO,
    @SerializedName("next_session") val nextSession: NextSessionDTO?
)

data class StudentOverviewDTO(
    val id: String,
    @SerializedName("display_name") val displayName: String,
    val xp: Int,
    val streak: Int,
    val level: Int
)

data class NextSessionDTO(
    val id: String,
    val title: String,
    val subject: String,
    @SerializedName("scheduled_at") val scheduledAt: String
)

data class ProfileResponse(
    val id: String,
    @SerializedName("display_name") val displayName: String,
    val email: String,
    val xp: Int,
    val streak: Int,
    val level: Int,
    @SerializedName("join_date") val joinDate: String
)
