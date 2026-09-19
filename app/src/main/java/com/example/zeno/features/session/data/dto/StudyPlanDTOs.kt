package com.example.zeno.features.session.data.dto

import com.google.gson.annotations.SerializedName

data class GenerateStudyPlanRequest(
    @SerializedName("available_minutes") val availableMinutes: Int
)

private val SUBJECT_AR_MAP = mapOf(
    "math" to "الرياضيات",
    "الرياضيات" to "الرياضيات",
    "physics" to "auto_str_الفيزياء",
    "auto_str_الفيزياء" to "auto_str_الفيزياء",
    "english" to "اللغة الإنجليزية",
    "اللغة الإنجليزية" to "اللغة الإنجليزية",
    "arabic" to "اللغة العربية",
    "اللغة العربية" to "اللغة العربية",
    "auto_str_عربي" to "اللغة العربية",
    "chemistry" to "الكيمياء",
    "الكيمياء" to "الكيمياء",
    "biology" to "الأحياء",
    "الأحياء" to "الأحياء",
    "general" to "عام"
)

private val SUBJECT_EN_MAP = mapOf(
    "math" to "Math",
    "الرياضيات" to "Math",
    "physics" to "Physics",
    "auto_str_الفيزياء" to "Physics",
    "english" to "English",
    "اللغة الإنجليزية" to "English",
    "arabic" to "Arabic",
    "اللغة العربية" to "Arabic",
    "auto_str_عربي" to "Arabic",
    "chemistry" to "Chemistry",
    "الكيمياء" to "Chemistry",
    "biology" to "Biology",
    "الأحياء" to "Biology",
    "general" to "General"
)

data class StudyPlanItemSchema(
    val id: String,
    @SerializedName("subject_id") val subjectId: String,
    @SerializedName("subject_name_ar") val subjectNameAr: String? = null,
    @SerializedName("subject_name_en") val subjectNameEn: String? = null,
    @SerializedName("topic_id") val topicId: String,
    val goal: String?,
    val mode: String,
    @SerializedName("planned_duration_minutes") val plannedDurationMinutes: Int,
    val priority: String,
    val reason: String?,
    @SerializedName("session_id") val sessionId: String?
) {
    fun getLocalizedSubject(language: String): String {
        val s = subjectId.trim().lowercase()
        return if (language == "ar") {
            when {
                !subjectNameAr.isNullOrBlank() -> subjectNameAr
                SUBJECT_AR_MAP.containsKey(s) -> SUBJECT_AR_MAP[s]!!
                SUBJECT_AR_MAP.containsKey(subjectId.trim()) -> SUBJECT_AR_MAP[subjectId.trim()]!!
                else -> subjectId
            }
        } else {
            when {
                !subjectNameEn.isNullOrBlank() -> subjectNameEn
                SUBJECT_EN_MAP.containsKey(s) -> SUBJECT_EN_MAP[s]!!
                SUBJECT_EN_MAP.containsKey(subjectId.trim()) -> SUBJECT_EN_MAP[subjectId.trim()]!!
                else -> subjectId
            }
        }
    }
}

data class StudyPlanSchema(
    val id: String,
    val version: Int,
    val status: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    val items: List<StudyPlanItemSchema>,
    @SerializedName("generated_at") val generatedAt: String
)
