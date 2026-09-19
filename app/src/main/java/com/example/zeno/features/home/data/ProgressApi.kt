package com.example.zeno.features.home.data

import com.example.zeno.features.home.data.dto.*
import com.example.zeno.features.session.data.dto.StartSessionRequest
import com.example.zeno.features.session.data.dto.SessionActionResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProgressApi {
    @GET("main/progress/overview")
    suspend fun getProgressOverview(): ProgressOverviewResponse

    @POST("main/progress/sessions")
    suspend fun startSession(@Body request: StartSessionRequest): SessionActionResponse

    @PUT("main/progress/sessions/{id}/complete")
    suspend fun completeSession(
        @Path("id") id: String, 
        @Body request: Any? = null
    ): SessionActionResponse

    @PUT("main/progress/sessions/{id}/cancel")
    suspend fun cancelSession(@Path("id") id: String): SessionActionResponse
}
