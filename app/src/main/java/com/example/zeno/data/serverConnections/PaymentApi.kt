package com.example.zeno.data.serverConnections

import com.example.zeno.data.model.server.PaymentResponse
import retrofit2.http.GET

interface PaymentApi {

    @GET("main/payments")
    suspend fun getPayments(): List<PaymentResponse>
}
