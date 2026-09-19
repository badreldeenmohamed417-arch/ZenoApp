package com.example.zeno.features.assessment.data

import com.google.gson.annotations.SerializedName
import java.util.UUID

enum class MessageSender {
    ZENO,
    USER
}

data class AssessmentChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val quickReplies: List<String> = emptyList(),
    val isProfileCard: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class EnglishSkillsProfile(
    @SerializedName("grammar") val grammar: String = "assessment_level_good",
    @SerializedName("vocabulary") val vocabulary: String = "assessment_level_needs_support",
    @SerializedName("reading") val reading: String = "assessment_level_good",
    @SerializedName("writing") val writing: String = "assessment_level_needs_support"
)

data class LearningProfile(
    @SerializedName("strengths") val strengths: List<String> = emptyList(),
    @SerializedName("focus_areas") val focusAreas: List<String> = emptyList(),
    @SerializedName("difficult_subjects") val difficultSubjects: List<String> = emptyList(),
    @SerializedName("preferred_explanation_style") val preferredExplanationStyle: String = "",
    @SerializedName("english_skills") val englishSkills: EnglishSkillsProfile = EnglishSkillsProfile(),
    @SerializedName("is_assessment_completed") val isAssessmentCompleted: Boolean = false
)
