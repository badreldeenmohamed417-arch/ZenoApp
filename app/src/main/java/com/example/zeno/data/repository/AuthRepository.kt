package com.example.zeno.data.repository

import com.example.zeno.data.model.server.CompleteDataRequest
import com.example.zeno.data.model.server.ForgotPasswordRequest
import com.example.zeno.data.model.server.GoogleLoginRequest
import com.example.zeno.data.model.server.LoginRequest
import com.example.zeno.data.model.server.RefreshRequest
import com.example.zeno.data.model.server.RegisterRequest
import com.example.zeno.data.model.server.RegisterResponse
import com.example.zeno.data.model.server.ResendVerificationRequest
import com.example.zeno.data.model.server.ResetPasswordRequest
import com.example.zeno.data.model.server.SimpleResponse
import com.example.zeno.data.model.server.TokenResponse
import com.example.zeno.data.model.server.VerifyEmailRequest
import com.example.zeno.data.server.ApiClient

class AuthRepository {

    private val api
        get() = ApiClient.auth()

    suspend fun register(
        email: String,
        password: String
    ): RegisterResponse {
        return api.register(
            RegisterRequest(
                email = email,
                password = password
            )
        )
    }

    suspend fun completeData(
        country: String? = null,
        displayName: String? = null,
        grade: String? = null,
        schoolSystem: String? = null,
        language: String = "ar"
    ) {
        val accessToken = ApiClient.tokenManager().getAccessToken()
            ?: error("No access token available.")

        val response = api.completeData(
            token = "Bearer $accessToken",
            request = CompleteDataRequest(
                country = country,
                displayName = displayName,
                grade = grade,
                schoolSystem = schoolSystem,
                language = language
            )
        )

        if (!response.isSuccessful) {
            error("Failed to complete user data: ${response.code()}")
        }
    }

    suspend fun login(
        email: String,
        password: String,
        deviceName: String? = null,
        platform: String? = null
    ): TokenResponse {
        val response = api.login(
            LoginRequest(
                email = email,
                password = password,
                deviceName = deviceName,
                platform = platform
            )
        )

        ApiClient.tokenManager().saveTokens(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken
        )

        return response
    }

    suspend fun googleLogin(
        idToken: String
    ): TokenResponse {
        val response = api.googleLogin(
            GoogleLoginRequest(
                idToken = idToken
            )
        )

        ApiClient.tokenManager().saveTokens(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken
        )

        return response
    }

    suspend fun refresh(): TokenResponse {
        val refreshToken = ApiClient.tokenManager()
            .getRefreshToken()
            ?: error("No refresh token available.")

        val response = api.refresh(
            RefreshRequest(refreshToken)
        )

        ApiClient.tokenManager().saveTokens(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken
        )

        return response
    }

    suspend fun forgotPassword(
        email: String,
        language: String = "ar"
    ) {
        api.forgotPassword(
            ForgotPasswordRequest(
                email = email,
                language = language
            )
        )
    }

    suspend fun resetPassword(
        token: String,
        newPassword: String
    ) {
        api.resetPassword(
            ResetPasswordRequest(
                token = token,
                newPassword = newPassword
            )
        )
    }

    // --- العمليات الجديدة الخاصة بالتحقق من الإيميل ---

    suspend fun verifyEmail(
        token: String
    ): SimpleResponse {
        return api.verifyEmail(
            VerifyEmailRequest(
                token = token
            )
        )
    }

    suspend fun resendVerification(
        email: String,
        language: String = "ar"
    ): SimpleResponse {
        return api.resendVerification(
            ResendVerificationRequest(
                email = email,
                language = language
            )
        )
    }

    suspend fun logout() {
        val accessToken = ApiClient.tokenManager().getAccessToken()
        if (accessToken != null) {
            runCatching {
                api.logout("Bearer $accessToken")
            }
        }
        ApiClient.tokenManager().clearTokens()
    }

    suspend fun logoutAll() {
        val accessToken = ApiClient.tokenManager().getAccessToken()
        if (accessToken != null) {
            runCatching {
                api.logoutAll("Bearer $accessToken")
            }
        }
        ApiClient.tokenManager().clearTokens()
    }
}