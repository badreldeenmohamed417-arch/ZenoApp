package com.example.zeno.features.student.data

import com.example.zeno.features.student.data.dto.ProfileResponse
import com.example.zeno.features.student.data.dto.StudentDashboardResponse
import retrofit2.http.GET

interface StudentApi {
    @GET("main/student/me/dashboard")
    suspend fun getDashboard(): StudentDashboardResponse

    @GET("main/student/me/profile")
    suspend fun getProfile(): ProfileResponse
}
