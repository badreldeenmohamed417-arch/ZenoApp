package com.example.zeno.features.premium.data.repository

import com.example.zeno.features.premium.data.SubscriptionApi
import com.example.zeno.features.premium.data.dto.PlanDto
import com.example.zeno.features.premium.data.dto.RedeemResponse
import com.example.zeno.data.model.server.SubscriptionResponse
import com.example.zeno.features.premium.data.dto.RedeemRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SubscriptionRepository(private val api: SubscriptionApi) {

    suspend fun getMySubscription(): SubscriptionResponse = withContext(Dispatchers.IO) {
        api.getMySubscription()
    }

    suspend fun getPlans(): Result<List<PlanDto>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getPlans()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun redeemCode(code: String): Result<RedeemResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.redeemCode(RedeemRequest(code))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
