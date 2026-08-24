package com.example.zeno.data.model.server

import com.google.gson.annotations.SerializedName

data class SubscriptionResponse(
    @SerializedName("current_plan") val currentPlan: String?,
    val status: String?,
    @SerializedName("started_at") val startedAt: String?,
    @SerializedName("expires_at") val expiresAt: String?,
    @SerializedName("available_token_balance") val availableTokenBalance: Int,
    val entitlements: Map<String, Any>
)

data class TokenBalanceResponse(
    val balance: Int,
    @SerializedName("total_earned") val totalEarned: Int,
    @SerializedName("total_spent") val totalSpent: Int
)

data class TokenTransactionResponse(
    val id: String,
    val type: String,
    val amount: Int,
    @SerializedName("balance_after") val balanceAfter: Int,
    val reason: String,
    @SerializedName("reference_id") val referenceId: String?,
    @SerializedName("created_at") val createdAt: String
)

data class PaymentResponse(
    val id: String,
    val amount: String,
    val currency: String,
    val provider: String,
    val status: String,
    @SerializedName("created_at") val createdAt: String
)
