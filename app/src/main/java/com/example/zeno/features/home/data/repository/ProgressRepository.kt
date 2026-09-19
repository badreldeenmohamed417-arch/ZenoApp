package com.example.zeno.features.home.data.repository

import com.example.zeno.features.home.data.ProgressApi
import com.example.zeno.features.home.data.dto.ProgressOverviewResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProgressRepository(private val api: ProgressApi) {
    suspend fun getProgressOverview(): Result<ProgressOverviewResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getProgressOverview()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
