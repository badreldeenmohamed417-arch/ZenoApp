package com.example.zeno.features.student.data.repository

import com.example.zeno.data.local.db.AppDatabase
import com.example.zeno.features.student.data.StudentApi
import com.example.zeno.features.student.data.dto.ProfileResponse
import com.example.zeno.features.student.data.dto.StudentDashboardResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class StudentRepository(private val api: StudentApi, private val database: AppDatabase) {
    suspend fun getDashboard(): Result<StudentDashboardResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getDashboard()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProfile(): Result<ProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getProfile()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(fields: Map<String, String?>): Result<ProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.updateProfile(fields)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestEmailChange(newEmail: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            api.requestEmailChange(mapOf("new_email" to newEmail))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestDataExport(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = api.requestDataExport()
            Result.success(response.message ?: "تم تقديم طلب تصدير البيانات بنجاح.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSettings(): Result<com.example.zeno.features.student.data.dto.SettingsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getSettings()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSettings(request: com.example.zeno.features.student.data.dto.UpdateSettingsRequest): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            api.updateSettings(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNotificationPreferences(): Result<com.example.zeno.features.student.data.dto.NotificationPreferencesResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getNotificationPreferences()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNotificationPreferences(request: com.example.zeno.features.student.data.dto.UpdateNotificationPreferencesRequest): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            api.updateNotificationPreferences(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCredentials(request: com.example.zeno.features.student.data.dto.UpdateCredentialsRequest): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            api.updateCredentials(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(request: com.example.zeno.features.student.data.dto.DeleteAccountRequest): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            api.deleteAccount(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteConversations(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            api.deleteConversations()
            database.chatDao().clearMessages()
            database.chatDao().clearConversations()
            Result.success(Unit)
        } catch (e: HttpException) {
            if (e.code() == 404) {
                database.chatDao().clearMessages()
                database.chatDao().clearConversations()
                return@withContext Result.success(Unit)
            }
            return@withContext Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
