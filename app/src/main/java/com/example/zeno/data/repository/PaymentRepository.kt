package com.example.zeno.data.repository

import com.example.zeno.data.model.server.PaymentResponse
import com.example.zeno.data.server.ApiClient

class PaymentRepository {

    suspend fun getPayments(): List<PaymentResponse> {
        return ApiClient.payments().getPayments()
    }
}
