package com.example.zeno.data.repository

import com.example.zeno.data.model.server.SubscriptionResponse
import com.example.zeno.data.server.ApiClient

class SubscriptionRepository {

    suspend fun getMySubscription(): SubscriptionResponse {
        return ApiClient.subscription().getMySubscription()
    }
}
