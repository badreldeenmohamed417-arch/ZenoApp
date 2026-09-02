package com.example.zeno.features.session.data

import com.example.zeno.features.session.data.dto.SessionActionResponse
import com.example.zeno.features.session.data.dto.SessionListResponse
import com.example.zeno.features.session.data.dto.StartSessionRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SessionApi {
    @GET("main/sessions")
    suspend fun getSessions(): SessionListResponse

    @POST("main/sessions/{id}/start")
    suspend fun startSession(@Path("id") id: String, @Body request: StartSessionRequest): SessionActionResponse

    @POST("main/sessions/{id}/end")
    suspend fun endSession(@Path("id") id: String): SessionActionResponse
}
