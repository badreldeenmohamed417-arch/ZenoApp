package com.example.zeno.features.content.data.dto

import com.google.gson.annotations.SerializedName

data class SubjectDTO(
    val id: String,
    val name: String,
    val description: String?,
    @SerializedName("icon_url") val iconUrl: String?
)

data class AssessmentQuestionDTO(
    val id: String,
    val question: String,
    val options: List<String>,
    @SerializedName("correct_option_index") val correctOptionIndex: Int,
    val explanation: String?
)
