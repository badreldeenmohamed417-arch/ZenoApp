package com.example.zeno.core.data

interface AuthStorage {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
}
