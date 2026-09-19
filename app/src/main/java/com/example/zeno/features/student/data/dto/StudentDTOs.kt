package com.example.zeno.features.student.data.dto

import com.google.gson.annotations.SerializedName
import com.example.zeno.data.model.server.Subject

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
    @SerializedName("display_name") val displayName: String?,
    val username: String? = null,
    val email: String,
    val country: String? = null,
    val grade: String? = null,
    @SerializedName("school_system") val schoolSystem: String? = null,
    val track: String? = null,
    @SerializedName("is_onboarded") val isOnboarded: Boolean? = null,
    @SerializedName("auth_provider") val authProvider: String? = "email",
    val xp: Int = 0,
    val streak: Int = 0,
    val level: Int = 1,
    @SerializedName("is_verified") val isVerified: Boolean = false,
    val subjects: List<Subject>? = null,
    @SerializedName("created_at") val joinDate: String? = null
)

data class SettingsResponse(
    val language: String,
    val theme: String
)

data class UpdateSettingsRequest(
    val language: String? = null,
    val theme: String? = null
)

data class NotificationPreferencesResponse(
    val timezone: String?,
    @SerializedName("quiet_hours_enabled") val quietHoursEnabled: Boolean,
    @SerializedName("quiet_hours_start") val quietHoursStart: String?,
    @SerializedName("quiet_hours_end") val quietHoursEnd: String?,
    @SerializedName("study_reminders") val studyReminders: Boolean,
    @SerializedName("plan_updates") val planUpdates: Boolean,
    @SerializedName("daily_summary") val dailySummary: Boolean
)

data class UpdateNotificationPreferencesRequest(
    val timezone: String,
    @SerializedName("quiet_hours_enabled") val quietHoursEnabled: Boolean,
    @SerializedName("quiet_hours_start") val quietHoursStart: String?,
    @SerializedName("quiet_hours_end") val quietHoursEnd: String?,
    @SerializedName("study_reminders") val studyReminders: Boolean,
    @SerializedName("plan_updates") val planUpdates: Boolean,
    @SerializedName("daily_summary") val dailySummary: Boolean
)

data class GenericMessageResponse(
    val message: String?
)

data class UpdateCredentialsRequest(
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("new_email") val newEmail: String? = null,
    @SerializedName("new_password") val newPassword: String? = null
)

data class DeleteAccountRequest(
    val password: String? = null,
    @SerializedName("google_id_token") val googleIdToken: String? = null
)
