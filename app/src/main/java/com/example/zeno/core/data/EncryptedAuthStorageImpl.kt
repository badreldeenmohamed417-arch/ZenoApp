package com.example.zeno.core.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedAuthStorageImpl(private val context: Context) : AuthStorage {

    private val sharedPreferences: SharedPreferences = createEncryptedSharedPreferences(context)

    override fun saveToken(token: String) {
        saveTokens(token, null)
    }

    override fun saveTokens(accessToken: String, refreshToken: String?) {
        sharedPreferences.edit()
            .putString(KEY_TOKEN, accessToken)
            .apply {
                if (!refreshToken.isNullOrBlank()) {
                    putString(KEY_REFRESH_TOKEN, refreshToken)
                }
            }
            .apply()

        // Sync to zeno_auth for TokenManager / ApiClient compatibility
        val oldPrefs = context.getSharedPreferences("zeno_auth", Context.MODE_PRIVATE)
        oldPrefs.edit().apply {
            putString("access_token", accessToken)
            if (!refreshToken.isNullOrBlank()) {
                putString("refresh_token", refreshToken)
            }
        }.apply()
    }

    override fun getToken(): String? {
        val token = try {
            sharedPreferences.getString(KEY_TOKEN, null)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading token from EncryptedSharedPreferences", e)
            null
        }
        if (!token.isNullOrBlank()) return token

        // Fallback: migrate from TokenManager zeno_auth
        val oldPrefs = context.getSharedPreferences("zeno_auth", Context.MODE_PRIVATE)
        val oldToken = oldPrefs.getString("access_token", null)
        if (!oldToken.isNullOrBlank()) {
            try {
                sharedPreferences.edit().putString(KEY_TOKEN, oldToken).apply()
            } catch (e: Exception) {
                Log.e(TAG, "Error saving migrated token", e)
            }
            return oldToken
        }
        return null
    }

    override fun getRefreshToken(): String? {
        val token = try {
            sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading refresh token", e)
            null
        }
        if (!token.isNullOrBlank()) return token

        val oldPrefs = context.getSharedPreferences("zeno_auth", Context.MODE_PRIVATE)
        return oldPrefs.getString("refresh_token", null)
    }

    override fun clearToken() {
        try {
            sharedPreferences.edit().remove(KEY_TOKEN).remove(KEY_REFRESH_TOKEN).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing tokens in EncryptedSharedPreferences", e)
        }
        val oldPrefs = context.getSharedPreferences("zeno_auth", Context.MODE_PRIVATE)
        oldPrefs.edit().clear().apply()
    }

    companion object {
        private const val TAG = "EncryptedAuthStorage"
        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"

        private fun createEncryptedSharedPreferences(context: Context): SharedPreferences {
            return try {
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create EncryptedSharedPreferences, clearing corrupted preferences...", e)
                try {
                    context.deleteSharedPreferences(PREFS_NAME)
                    val masterKey = MasterKey.Builder(context)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build()

                    EncryptedSharedPreferences.create(
                        context,
                        PREFS_NAME,
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )
                } catch (retryException: Exception) {
                    Log.e(TAG, "Failed again to create EncryptedSharedPreferences, falling back to standard SharedPreferences", retryException)
                    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                }
            }
        }
    }
}
