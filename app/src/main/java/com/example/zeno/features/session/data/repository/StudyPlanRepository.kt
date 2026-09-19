package com.example.zeno.features.session.data.repository

import com.example.zeno.features.session.data.StudyPlanApi
import com.example.zeno.features.session.data.dto.GenerateStudyPlanRequest
import com.example.zeno.features.session.data.dto.StudyPlanSchema
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StudyPlanRepository(private val api: StudyPlanApi) {
    suspend fun getCurrentStudyPlan(): Result<StudyPlanSchema> = withContext(Dispatchers.IO) {
        try {
            val response = api.getCurrentStudyPlan()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun generateStudyPlan(minutes: Int): Result<StudyPlanSchema> = withContext(Dispatchers.IO) {
        try {
            val response = api.generateStudyPlan(GenerateStudyPlanRequest(minutes))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
