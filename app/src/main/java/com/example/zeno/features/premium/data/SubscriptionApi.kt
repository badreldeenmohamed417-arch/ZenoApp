package com.example.zeno.features.premium.data

import com.example.zeno.features.premium.data.dto.PlanDto
import com.example.zeno.features.premium.data.dto.RedeemRequest
import com.example.zeno.features.premium.data.dto.RedeemResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SubscriptionApi {
    @GET("main/subscription/my")
    suspend fun getMySubscription(): com.example.zeno.data.model.server.SubscriptionResponse

    @GET("main/subscription/plans")
    suspend fun getPlans(): List<PlanDto>

    @POST("main/subscription/redeem")
    suspend fun redeemCode(@Body request: RedeemRequest): RedeemResponse
}
