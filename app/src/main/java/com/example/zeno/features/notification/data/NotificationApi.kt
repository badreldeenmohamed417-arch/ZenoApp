package com.example.zeno.features.notification.data

import retrofit2.http.Body
import retrofit2.http.POST

data class DeviceTokenRequest(
    val token: String,
    val platform: String = "android",
    val device_name: String? = null
)

interface NotificationApi {
    @POST("main/notifications/devices")
    suspend fun registerDevice(@Body request: DeviceTokenRequest)
}
