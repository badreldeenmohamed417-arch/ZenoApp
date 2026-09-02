package com.example.zeno.features.student.data.repository

import com.example.zeno.features.student.data.StudentApi
import com.example.zeno.features.student.data.dto.ProfileResponse
import com.example.zeno.features.student.data.dto.StudentDashboardResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StudentRepository(private val api: StudentApi) {
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
}
