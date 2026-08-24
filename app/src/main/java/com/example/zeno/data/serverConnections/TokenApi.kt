package com.example.zeno.data.serverConnections

import com.example.zeno.data.model.server.TokenBalanceResponse
import com.example.zeno.data.model.server.TokenTransactionResponse
import retrofit2.http.GET

interface TokenApi {

    @GET("main/tokens/me")
    suspend fun getBalance(): TokenBalanceResponse

    @GET("main/tokens/transactions")
    suspend fun getTransactions(): List<TokenTransactionResponse>
}
