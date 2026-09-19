package com.example.zeno.core.data

interface AuthStorage {
    fun saveToken(token: String)
    fun saveTokens(accessToken: String, refreshToken: String? = null)
    fun getToken(): String?
    fun getRefreshToken(): String?
    fun clearToken()
}
