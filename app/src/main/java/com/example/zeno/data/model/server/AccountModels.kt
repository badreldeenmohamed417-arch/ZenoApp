package com.example.zeno.data.model.server

data class SubscriptionResponse(
    val current_plan: String?,
    val status: String?,
    val started_at: String?,
    val expires_at: String?,
    val available_token_balance: Int,
    val entitlements: Map<String, Any>
)

data class TokenBalanceResponse(
    val balance: Int,
    val total_earned: Int,
    val total_spent: Int
)

data class TokenTransactionResponse(
    val id: String,
    val type: String,
    val amount: Int,
    val balance_after: Int,
    val reason: String,
    val reference_id: String?,
    val created_at: String
)

data class PaymentResponse(
    val id: String,
    val amount: String,
    val currency: String,
    val provider: String,
    val status: String,
    val created_at: String
)
