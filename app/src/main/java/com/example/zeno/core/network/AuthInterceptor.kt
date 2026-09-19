package com.example.zeno.core.network

import com.example.zeno.core.data.AuthStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authStorage: AuthStorage) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        val requestBuilder = originalRequest.newBuilder()

        // Don't add auth token to auth initialization endpoints
        val isAuthEndpoint = path.endsWith("/login") || 
                path.endsWith("/register") || 
                path.endsWith("/google") || 
                path.endsWith("/forgot-password")

        if (!isAuthEndpoint) {
            val token = authStorage.getToken()?.trim()
            if (!token.isNullOrBlank()) {
                val cleanToken = if (token.startsWith("Bearer ", ignoreCase = true)) {
                    token
                } else {
                    "Bearer $token"
                }
                requestBuilder.header("Authorization", cleanToken)
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}
