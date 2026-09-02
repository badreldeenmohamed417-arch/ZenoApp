package com.example.zeno.features.session.data.repository

import com.example.zeno.features.session.data.SessionApi
import com.example.zeno.features.session.data.dto.SessionActionResponse
import com.example.zeno.features.session.data.dto.SessionListResponse
import com.example.zeno.features.session.data.dto.StartSessionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SessionRepository(private val sessionApi: SessionApi) {
    suspend fun getSessions(): Result<SessionListResponse> = withContext(Dispatchers.IO) {
        try {
            val response = sessionApi.getSessions()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun startSession(id: String): Result<SessionActionResponse> = withContext(Dispatchers.IO) {
        try {
            val response = sessionApi.startSession(id, StartSessionRequest(id))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
