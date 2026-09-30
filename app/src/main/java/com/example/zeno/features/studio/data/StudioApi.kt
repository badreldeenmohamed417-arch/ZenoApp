package com.example.zeno.features.studio.data

import com.example.zeno.features.studio.data.dto.GenerateMaterialRequest
import com.example.zeno.features.studio.data.dto.GenerateQuizRequest
import com.example.zeno.features.studio.data.dto.StudioResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface StudioApi {
    @POST("api/studio/generate-material")
    suspend fun generateMaterial(@Body request: GenerateMaterialRequest): StudioResponse

    @POST("api/studio/generate-quiz")
    suspend fun generateQuiz(@Body request: GenerateQuizRequest): StudioResponse
}
