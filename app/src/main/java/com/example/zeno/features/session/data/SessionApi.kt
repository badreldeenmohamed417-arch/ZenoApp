package com.example.zeno.features.session.data

import com.example.zeno.features.session.data.dto.CompleteStudySessionPayload
import com.example.zeno.features.session.data.dto.SessionActionResponse
import com.example.zeno.features.session.data.dto.SessionListResponse
import com.example.zeno.features.session.data.dto.StartSessionRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SessionApi {
    @GET("main/progress/sessions/recent")
    suspend fun getSessions(): SessionListResponse

    @POST("main/progress/sessions")
    suspend fun startSession(@Body request: StartSessionRequest): SessionActionResponse

    @POST("main/progress/sessions/{id}/complete")
    suspend fun completeSession(
        @Path("id") sessionId: String,
        @Body payload: CompleteStudySessionPayload = CompleteStudySessionPayload()
    ): SessionActionResponse
}
