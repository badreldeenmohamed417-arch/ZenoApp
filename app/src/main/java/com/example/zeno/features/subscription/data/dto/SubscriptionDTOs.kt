package com.example.zeno.features.subscription.data.dto

import com.google.gson.annotations.SerializedName

data class SubscriptionResponse(
    @SerializedName("current_plan") val currentPlan: String?,
    val status: String?,
    @SerializedName("started_at") val startedAt: String?,
    @SerializedName("expires_at") val expiresAt: String?,
    @SerializedName("available_token_balance") val availableTokenBalance: Int,
    val entitlements: Map<String, Any>
)
