package com.example.zeno.data.local

import android.content.Context
import com.example.zeno.core.data.EncryptedAuthStorageImpl

class TokenManager(context: Context) {

    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(
        "zeno_auth",
        Context.MODE_PRIVATE
    )
    private val encryptedStorage = EncryptedAuthStorageImpl(appContext)

    fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) {
        preferences.edit()
            .putString("access_token", accessToken)
            .putString("refresh_token", refreshToken)
            .apply()

        // Sync to encrypted storage
        try {
            encryptedStorage.saveTokens(accessToken, refreshToken)
        } catch (_: Exception) {
            // Ignore if encryption context issue
        }
    }

    fun getAccessToken(): String? {
        val token = preferences.getString("access_token", null)
        if (!token.isNullOrBlank()) return token
        return encryptedStorage.getToken()
    }

    fun getRefreshToken(): String? {
        val token = preferences.getString("refresh_token", null)
        if (!token.isNullOrBlank()) return token
        return encryptedStorage.getRefreshToken()
    }

    fun clearTokens() {
        preferences.edit().clear().apply()
        try {
            encryptedStorage.clearToken()
        } catch (_: Exception) {
            // Ignore
        }
    }
}
