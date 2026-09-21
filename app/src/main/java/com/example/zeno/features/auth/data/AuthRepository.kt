package com.example.zeno.features.auth.data

import com.example.zeno.core.data.AuthStorage
import com.example.zeno.data.local.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AuthRepository(
    private val authApi: AuthApi,
    private val authStorage: AuthStorage,
    private val userManager: UserManager,
    private val context: Context
) {

    suspend fun login(request: LoginRequest): Result<TokenResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.login(request)
            authStorage.saveTokens(response.accessToken, response.refreshToken)
            userManager.saveOnboardingStatus(response.isOnboarded == true)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun googleLogin(idToken: String): Result<TokenResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.googleLogin(GoogleLoginRequest(idToken))
            authStorage.saveTokens(response.accessToken, response.refreshToken)
            userManager.saveOnboardingStatus(response.isOnboarded == true)
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
            userManager.saveOnboardingStatus(true)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyEmail(request: VerifyEmailRequest): Result<BaseResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.verifyEmail(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resendVerification(request: ResendVerificationRequest): Result<BaseResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.resendVerification(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        authStorage.clearToken()
        userManager.clearUserData()
        CoroutineScope(Dispatchers.IO).launch {
            com.example.zeno.data.local.db.AppDatabase.getDatabase(context).clearAllTables()
        }
    }

    fun isLoggedIn(): Boolean {
        return !authStorage.getToken().isNullOrBlank()
    }
}
