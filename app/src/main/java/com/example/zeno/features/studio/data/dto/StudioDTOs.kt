package com.example.zeno.features.studio.data.dto

import com.google.gson.annotations.SerializedName

data class GenerateMaterialRequest(
    @SerializedName("subject") val subject: String,
    @SerializedName("lessons") val lessons: List<String>,
    @SerializedName("pages") val pages: Int
)

data class GenerateQuizRequest(
    @SerializedName("subject") val subject: String,
    @SerializedName("lessons") val lessons: List<String>
)

data class StudioResponse(
    @SerializedName("content") val content: String? = null,
    @SerializedName("cost_deducted") val costDeducted: Int? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null
)
