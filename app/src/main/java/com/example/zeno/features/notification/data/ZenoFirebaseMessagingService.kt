package com.example.zeno.features.notification.data

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ZenoFirebaseMessagingService : FirebaseMessagingService() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    
    // Ideally injected, but for now we need a way to get the api.
    // In a real app, you'd use Dagger/Hilt or Koin, or fetch it from a singleton NetworkModule.
    // This is a placeholder for where the token registration happens.
    
    override fun onNewToken(token: String) {
        Log.d("ZenoFCM", "Refreshed token: $token")
        // Send token to server
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("ZenoFCM", "From: ${remoteMessage.from}")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("ZenoFCM", "Message data payload: ${remoteMessage.data}")
        }
        remoteMessage.notification?.let {
            Log.d("ZenoFCM", "Message Notification Body: ${it.body}")
            // The system handles notifications automatically when app is in background.
            // If we are in the foreground, we could show a local notification here.
        }
    }
    
    private fun sendRegistrationToServer(token: String) {
        scope.launch {
            try {
                try {
                    com.example.zeno.data.server.ApiClient.notification()
                } catch (e: Exception) {
                    com.example.zeno.data.server.ApiClient.initialize(applicationContext)
                }
                
                com.example.zeno.data.server.ApiClient.notification().registerDevice(
                    DeviceTokenRequest(token = token)
                )
                Log.d("ZenoFCM", "Token sent to server successfully")
            } catch (e: Exception) {
                Log.e("ZenoFCM", "Failed to send token to server", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
