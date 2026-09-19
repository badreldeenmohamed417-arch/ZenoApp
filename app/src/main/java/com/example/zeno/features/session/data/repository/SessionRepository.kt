package com.example.zeno.features.session.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.zeno.features.session.data.SessionApi
import com.example.zeno.features.session.data.dto.CompleteStudySessionPayload
import com.example.zeno.features.session.data.dto.SessionActionResponse
import com.example.zeno.features.session.data.dto.SessionListResponse
import com.example.zeno.features.session.data.dto.StartSessionRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

data class PendingSessionStart(
    val localId: String,
    val subjectId: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class PendingSessionComplete(
    val sessionId: String,
    val subjectId: String,
    val minutes: Int,
    val timestamp: Long = System.currentTimeMillis()
)

class SessionRepository(
    private val sessionApi: SessionApi,
    private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("pending_sessions_v2", Context.MODE_PRIVATE)
    private val gson = Gson()

    suspend fun getSessions(): Result<SessionListResponse> = withContext(Dispatchers.IO) {
        try {
            val response = sessionApi.getSessions()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun startSession(subjectId: String): Result<SessionActionResponse> = withContext(Dispatchers.IO) {
        try {
            val response = sessionApi.startSession(StartSessionRequest(subjectId))
            Result.success(response)
        } catch (e: Exception) {
            // Save as offline pending start
            val localId = "local_" + UUID.randomUUID().toString()
            savePendingStart(PendingSessionStart(localId, subjectId))
            Result.success(
                SessionActionResponse(
                    sessionId = localId,
                    subjectId = subjectId,
                    status = "active",
                    minutes = 0,
                    questionsCount = 0
                )
            )
        }
    }

    suspend fun completeSession(sessionId: String, minutes: Int = 0): Result<SessionActionResponse> = withContext(Dispatchers.IO) {
        try {
            val response = sessionApi.completeSession(
                sessionId = sessionId,
                payload = CompleteStudySessionPayload(minutes = minutes)
            )
            Result.success(response)
        } catch (e: Exception) {
            // Save as offline pending complete
            savePendingComplete(PendingSessionComplete(sessionId, "", minutes))
            Result.failure(e)
        }
    }

    suspend fun syncPendingSessions() = withContext(Dispatchers.IO) {
        // 1. Sync Pending Starts
        val pendingStarts = getPendingStarts()
        for (start in pendingStarts) {
            try {
                val res = sessionApi.startSession(StartSessionRequest(start.subjectId))
                val newServerId = res.sessionId
                if (newServerId != null) {
                    removePendingStart(start.localId)
                    // Update any matching pending complete that referenced localId
                    val completes = getPendingCompletes()
                    completes.filter { it.sessionId == start.localId }.forEach {
                        removePendingComplete(start.localId)
                        savePendingComplete(it.copy(sessionId = newServerId))
                    }
                }
            } catch (_: Exception) {}
        }

        // 2. Sync Pending Completes
        val pendingCompletes = getPendingCompletes()
        for (complete in pendingCompletes) {
            if (complete.sessionId.startsWith("local_")) continue // wait until start syncs
            try {
                sessionApi.completeSession(
                    sessionId = complete.sessionId,
                    payload = CompleteStudySessionPayload(minutes = complete.minutes)
                )
                removePendingComplete(complete.sessionId)
            } catch (_: Exception) {}
        }
    }

    private fun savePendingStart(start: PendingSessionStart) {
        val starts = getPendingStarts().toMutableList()
        starts.removeAll { it.localId == start.localId }
        starts.add(start)
        prefs.edit().putString("starts", gson.toJson(starts)).apply()
    }

    private fun getPendingStarts(): List<PendingSessionStart> {
        val json = prefs.getString("starts", null) ?: return emptyList()
        val type = object : TypeToken<List<PendingSessionStart>>() {}.type
        return try { gson.fromJson(json, type) } catch (e: Exception) { emptyList() }
    }

    private fun removePendingStart(localId: String) {
        val starts = getPendingStarts().filterNot { it.localId == localId }
        prefs.edit().putString("starts", gson.toJson(starts)).apply()
    }

    private fun savePendingComplete(complete: PendingSessionComplete) {
        val completes = getPendingCompletes().toMutableList()
        completes.removeAll { it.sessionId == complete.sessionId }
        completes.add(complete)
        prefs.edit().putString("completes", gson.toJson(completes)).apply()
    }

    private fun getPendingCompletes(): List<PendingSessionComplete> {
        val json = prefs.getString("completes", null) ?: return emptyList()
        val type = object : TypeToken<List<PendingSessionComplete>>() {}.type
        return try { gson.fromJson(json, type) } catch (e: Exception) { emptyList() }
    }

    private fun removePendingComplete(sessionId: String) {
        val completes = getPendingCompletes().filterNot { it.sessionId == sessionId }
        prefs.edit().putString("completes", gson.toJson(completes)).apply()
    }
}
