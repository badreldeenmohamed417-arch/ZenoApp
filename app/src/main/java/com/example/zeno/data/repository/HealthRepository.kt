package com.example.zeno.data.repository

import com.example.zeno.data.server.ApiClient
import com.example.zeno.data.serverConnections.HealthResponse
import com.example.zeno.data.serverConnections.ReadyResponse

class HealthRepository {

    suspend fun home(): HealthResponse {
        return ApiClient.health().home()
    }

    suspend fun live(): HealthResponse {
        return ApiClient.health().live()
    }

    suspend fun ready(): ReadyResponse {
        return ApiClient.health().ready()
    }
}
