package com.example.zeno.features.auth.data

import com.example.zeno.core.data.AuthStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val authApi: AuthApi,
    private val authStorage: AuthStorage
) {

    suspend fun login(request: LoginRequest): Result<TokenResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.login(request)
            authStorage.saveToken(response.accessToken)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun googleLogin(idToken: String): Result<TokenResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.googleLogin(GoogleLoginRequest(idToken))
            authStorage.saveToken(response.accessToken)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<RegisterResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.register(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(request: ForgotPasswordRequest): Result<BaseResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.forgotPassword(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeData(request: CompleteDataRequest): Result<UserResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.completeData(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        authStorage.clearToken()
    }

    fun isLoggedIn(): Boolean {
        return !authStorage.getToken().isNullOrBlank()
    }
}
