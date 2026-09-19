package com.example.zeno.features.session.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.zeno.features.session.data.StudyPlanApi
import com.example.zeno.features.session.data.dto.GenerateStudyPlanRequest
import com.example.zeno.features.session.data.dto.StudyPlanSchema
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StudyPlanRepository(private val api: StudyPlanApi): KoinComponent {
    private val context: Context by inject()
    private val prefs: SharedPreferences = context.getSharedPreferences("plan_cache", Context.MODE_PRIVATE)
    private val gson = Gson()

    suspend fun getCurrentStudyPlan(): Result<StudyPlanSchema> = withContext(Dispatchers.IO) {
        try {
            val response = api.getCurrentStudyPlan()
            prefs.edit().putString("saved_plan", gson.toJson(response)).apply()
            Result.success(response)
        } catch (e: Exception) {
            val cached = prefs.getString("saved_plan", null)
            if (cached != null) {
                try {
                    val plan = gson.fromJson(cached, StudyPlanSchema::class.java)
                    Result.success(plan)
                } catch (ex: Exception) {
                    Result.failure(e)
                }
            } else {
                Result.failure(e)
            }
        }
    }
    
    suspend fun generateStudyPlan(minutes: Int): Result<StudyPlanSchema> = withContext(Dispatchers.IO) {
        try {
            val response = api.generateStudyPlan(GenerateStudyPlanRequest(minutes))
            prefs.edit().putString("saved_plan", gson.toJson(response)).apply()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
