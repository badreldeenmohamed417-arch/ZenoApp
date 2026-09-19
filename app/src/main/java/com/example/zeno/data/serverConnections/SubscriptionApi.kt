package com.example.zeno.data.serverConnections

import com.example.zeno.data.model.server.SubscriptionResponse
import com.example.zeno.features.premium.data.dto.PlanDto
import com.example.zeno.features.premium.data.dto.RedeemResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class RedeemRequest(val code: String)

interface SubscriptionApi {

    @GET("main/subscription/me")
    suspend fun getMySubscription(): SubscriptionResponse

    @GET("main/subscription/plans")
    suspend fun getPlans(): List<PlanDto>

    @POST("main/subscription/redeem")
    suspend fun redeemCode(@Body request: RedeemRequest): RedeemResponse
}
