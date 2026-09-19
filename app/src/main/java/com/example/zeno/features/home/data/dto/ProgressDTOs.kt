package com.example.zeno.features.home.data.dto

import com.google.gson.annotations.SerializedName

data class ProgressOverviewResponse(
    val greeting: String?,
    val date: String?,
    @SerializedName("daily_goal") val dailyGoal: String?,
    @SerializedName("today_progress") val todayProgress: TodayProgressDTO?,
    @SerializedName("next_session") val nextSession: NextSessionDTO?,
    @SerializedName("weak_topics") val weakTopics: List<WeakTopicDTO>?,
    val streak: Int?,
    val level: Int?,
    @SerializedName("questions_asked_today") val questionsAskedToday: Int?,
    @SerializedName("minutes_today") val minutesToday: Int?,
    @SerializedName("weekly_study_time") val weeklyStudyTime: Map<String, Int>?,
    @SerializedName("subject_mastery") val subjectMastery: List<SubjectMasteryDTO>?,
    @SerializedName("today_plan") val todayPlan: List<TodayPlanDTO>?,
    @SerializedName("is_limit_reached") val isLimitReached: Boolean? = false
)

data class TodayProgressDTO(
    @SerializedName("planned_minutes") val plannedMinutes: Int,
    @SerializedName("completed_minutes") val completedMinutes: Int,
    @SerializedName("completed_sessions") val completedSessions: Int,
    @SerializedName("total_sessions") val totalSessions: Int
)

data class NextSessionDTO(
    val id: String,
    val title: String?,
    @SerializedName("subject_id") val subjectId: String,
    @SerializedName("topic_id") val topicId: String,
    val status: String
)

data class WeakTopicDTO(
    @SerializedName("subject_id") val subjectId: String,
    @SerializedName("topic_id") val topicId: String,
    val mastery: Double
)

data class SubjectMasteryDTO(
    val name: String,
    val percentage: Int
)

data class TodayPlanDTO(
    val id: String,
    val subject: String,
    val time: String,
    val isCompleted: Boolean
)
