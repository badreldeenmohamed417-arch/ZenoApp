package com.example.zeno.data.repository

import com.example.zeno.data.model.server.TokenBalanceResponse
import com.example.zeno.data.model.server.TokenTransactionResponse
import com.example.zeno.data.server.ApiClient

class TokenRepository {

    suspend fun getBalance(): TokenBalanceResponse {
        return ApiClient.tokens().getBalance()
    }

    suspend fun getTransactions(): List<TokenTransactionResponse> {
        return ApiClient.tokens().getTransactions()
    }
}
