package com.example.zeno.data.serverConnections

import com.example.zeno.data.model.server.SubscriptionResponse
import retrofit2.http.GET

interface SubscriptionApi {

    @GET("subscription/me")
    suspend fun getMySubscription(): SubscriptionResponse
}
