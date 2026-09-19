package com.example.zeno.core.config.data

import com.google.gson.annotations.SerializedName

data class AppConfigResponse(
    @SerializedName("study_durations") val studyDurations: List<Int>,
    val avatars: List<String>,
    @SerializedName("assessment_options") val assessmentOptions: AssessmentOptionsDto,
    @SerializedName("default_subjects") val defaultSubjects: List<String>,
    @SerializedName("support_email") val supportEmail: String? = null,
    @SerializedName("terms_url") val termsUrl: String? = null,
    @SerializedName("privacy_url") val privacyUrl: String? = null,
    @SerializedName("chat_welcome_title") val chatWelcomeTitle: String? = null,
    @SerializedName("chat_welcome_subtitle") val chatWelcomeSubtitle: String? = null
)

data class AssessmentOptionsDto(
    val strengths: List<String>,
    @SerializedName("focus_areas") val focusAreas: List<String>,
    @SerializedName("explanation_styles") val explanationStyles: List<String>,
    @SerializedName("initial_questions") val initialQuestions: List<AssessmentQuestionDto>,
    @SerializedName("subject_topics") val subjectTopics: Map<String, List<String>>
)

data class AssessmentQuestionDto(
    val subject: String,
    val question: String,
    val options: List<String>
)
