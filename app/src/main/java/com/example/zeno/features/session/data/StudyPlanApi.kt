package com.example.zeno.features.session.data

import com.example.zeno.features.session.data.dto.GenerateStudyPlanRequest
import com.example.zeno.features.session.data.dto.StudyPlanSchema
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface StudyPlanApi {
    @GET("main/study-plans/current")
    suspend fun getCurrentStudyPlan(): StudyPlanSchema
    
    @POST("main/study-plans/generate")
    suspend fun generateStudyPlan(@Body request: GenerateStudyPlanRequest): StudyPlanSchema
}
