package com.example.zeno.features.subscription.data

import com.example.zeno.features.subscription.data.dto.SubscriptionResponse
import retrofit2.http.GET

interface SubscriptionApi {
    @GET("main/subscription/me")
    suspend fun getMySubscription(): SubscriptionResponse
}
