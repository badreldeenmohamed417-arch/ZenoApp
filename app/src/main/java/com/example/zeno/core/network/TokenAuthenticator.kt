package com.example.zeno.core.network

import android.content.Context
import com.example.zeno.core.data.AuthStorage
import com.example.zeno.features.auth.data.AuthApi
import com.example.zeno.features.auth.data.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenAuthenticator(
    private val authStorage: AuthStorage,
    private val context: Context,
    private val baseUrl: String
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            val path = response.request.url.encodedPath
            if (path.contains("/auth/login") || path.contains("/auth/register") || path.contains("/auth/refresh") || path.contains("/auth/google")) {
                return null
            }

            synchronized(this) {
                val currentToken = authStorage.getToken()?.trim()
                val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")?.trim()

                // If token changed while waiting, retry with new token
                if (!currentToken.isNullOrBlank() && currentToken != requestToken) {
                    return response.request.newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .build()
                }

                val refreshToken = authStorage.getRefreshToken()?.trim()

                if (!refreshToken.isNullOrBlank()) {
                    try {
                        val refreshRetrofit = Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                        val authApi = refreshRetrofit.create(AuthApi::class.java)
                        
                        val refreshResponse = runBlocking {
                            authApi.refreshToken(RefreshTokenRequest(refreshToken))
                        }

                        authStorage.saveTokens(
                            accessToken = refreshResponse.accessToken,
                            refreshToken = refreshResponse.refreshToken ?: ""
                        )

                        return response.request.newBuilder()
                            .header("Authorization", "Bearer ${refreshResponse.accessToken}")
                            .build()
                    } catch (e: Exception) {
                        authStorage.clearToken()
                        showSessionExpiredToast()
                    }
                } else {
                    authStorage.clearToken()
                    showSessionExpiredToast()
                }
            }
        }
        return null
    }

    private fun showSessionExpiredToast() {
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            val message = "Your session has expired. Please log in again."
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show()

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            val channelId = "session_channel"
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel = android.app.NotificationChannel(channelId, "Session", android.app.NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }
            val intent = android.content.Intent(context, com.example.zeno.MainActivity::class.java).apply {
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = android.app.PendingIntent.getActivity(context, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT)
            val builder = androidx.core.app.NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Session Expired")
                .setContentText(message)
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            
            notificationManager.notify(1001, builder.build())
        }
    }
}
