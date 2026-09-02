package com.example.zeno.features.notification.data

interface PushNotificationService {
    fun initialize()
    suspend fun registerDeviceToken(token: String)
}

// Dummy implementation since actual FCM requires google-services.json which we don't have.
class ZenoFirebaseMessagingServiceImpl : PushNotificationService {
    override fun initialize() {
        // FirebaseMessaging.getInstance().token.addOnCompleteListener { ... }
    }

    override suspend fun registerDeviceToken(token: String) {
        // Send token to MainServer
    }
}
