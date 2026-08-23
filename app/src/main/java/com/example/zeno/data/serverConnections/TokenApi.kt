package com.example.zeno.data.serverConnections

import com.example.zeno.data.model.server.TokenBalanceResponse
import com.example.zeno.data.model.server.TokenTransactionResponse
import retrofit2.http.GET

interface TokenApi {

    @GET("tokens/me")
    suspend fun getBalance(): TokenBalanceResponse

    @GET("tokens/transactions")
    suspend fun getTransactions(): List<TokenTransactionResponse>
}
