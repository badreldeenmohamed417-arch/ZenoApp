package com.example.zeno.data.serverConnections

import retrofit2.http.GET

data class HealthResponse(
    val status: String
)

data class ReadyResponse(
    val status: String,
    val database: String
)

interface HealthApi {

    @GET("/")
    suspend fun home(): HealthResponse

    @GET("health/live")
    suspend fun live(): HealthResponse

    @GET("health/ready")
    suspend fun ready(): ReadyResponse
}
