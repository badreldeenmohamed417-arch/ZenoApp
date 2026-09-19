package com.example.zeno.features.student.data

import com.example.zeno.features.student.data.dto.GenericMessageResponse
import com.example.zeno.features.student.data.dto.ProfileResponse
import com.example.zeno.features.student.data.dto.StudentDashboardResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface StudentApi {
    @GET("main/users/dashboard")
    suspend fun getDashboard(): StudentDashboardResponse

    @GET("main/users/me")
    suspend fun getProfile(): ProfileResponse

    @PATCH("main/users/me")
    suspend fun updateProfile(@Body request: Map<String, String?>): ProfileResponse

    @POST("main/users/me/request-email-change")
    suspend fun requestEmailChange(@Body request: Map<String, String>): GenericMessageResponse

    @POST("main/users/me/export-data")
    suspend fun requestDataExport(): GenericMessageResponse

    @GET("main/users/settings")
    suspend fun getSettings(): com.example.zeno.features.student.data.dto.SettingsResponse

    @retrofit2.http.PATCH("main/users/settings")
    suspend fun updateSettings(@retrofit2.http.Body request: com.example.zeno.features.student.data.dto.UpdateSettingsRequest): com.example.zeno.features.student.data.dto.GenericMessageResponse

    @GET("main/notifications/preferences")
    suspend fun getNotificationPreferences(): com.example.zeno.features.student.data.dto.NotificationPreferencesResponse

    @retrofit2.http.PUT("main/notifications/preferences")
    suspend fun updateNotificationPreferences(@retrofit2.http.Body request: com.example.zeno.features.student.data.dto.UpdateNotificationPreferencesRequest): com.example.zeno.features.student.data.dto.GenericMessageResponse

    @retrofit2.http.PATCH("main/users/me/credentials")
    suspend fun updateCredentials(@retrofit2.http.Body request: com.example.zeno.features.student.data.dto.UpdateCredentialsRequest): com.example.zeno.features.student.data.dto.GenericMessageResponse

    @retrofit2.http.HTTP(method = "DELETE", path = "main/users/me", hasBody = true)
    suspend fun deleteAccount(@retrofit2.http.Body request: com.example.zeno.features.student.data.dto.DeleteAccountRequest): com.example.zeno.features.student.data.dto.GenericMessageResponse

    @retrofit2.http.DELETE("main/users/me/conversations")
    suspend fun deleteConversations()
}
