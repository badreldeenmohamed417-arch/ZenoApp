package com.example.zeno.features.home.data.repository

import com.example.zeno.features.home.data.ProgressApi
import com.example.zeno.features.home.data.HomeCacheManager
import com.example.zeno.features.home.data.dto.ProgressOverviewResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProgressRepository(
    private val api: ProgressApi,
    private val cacheManager: HomeCacheManager
) {
    fun getCachedProgress(): ProgressOverviewResponse? = cacheManager.getProgress()
    
    suspend fun getProgressOverview(): Result<ProgressOverviewResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getProgressOverview()
            cacheManager.saveProgress(response)
            Result.success(response)
        } catch (e: Exception) {
            val cached = cacheManager.getProgress()
            if (cached != null) {
                Result.success(cached)
            } else {
                Result.failure(e)
            }
        }
    }
}
