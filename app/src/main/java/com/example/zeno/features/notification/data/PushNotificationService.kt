package com.example.zeno.features.notification.data

import kotlinx.coroutines.launch

interface PushNotificationService {
    fun initialize()
    suspend fun registerDeviceToken(token: String)
}

// Dummy implementation replaced with actual FCM
class ZenoFirebaseMessagingServiceImpl : PushNotificationService {
    override fun initialize() {
        try {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    android.util.Log.w("ZenoFCM", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                android.util.Log.d("ZenoFCM", "FCM Token initialized: $token")
                
                kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    registerDeviceToken(token)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ZenoFCM", "Error initializing FCM", e)
        }
    }

    override suspend fun registerDeviceToken(token: String) {
        try {
            com.example.zeno.data.server.ApiClient.notification().registerDevice(
                com.example.zeno.features.notification.data.DeviceTokenRequest(token = token)
            )
            android.util.Log.d("ZenoFCM", "Token sent to server successfully via interface")
        } catch (e: Exception) {
            android.util.Log.e("ZenoFCM", "Failed to send token to server via interface", e)
        }
    }
}
