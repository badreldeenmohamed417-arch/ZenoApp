package com.example.zeno.data.serverConnections

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
import com.example.zeno.data.model.server.UserResponse
import com.example.zeno.data.model.server.VerifyEmailRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

    @POST("main/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("main/auth/complete-data")
    suspend fun completeData(
        @Body request: CompleteDataRequest
    ): UserResponse

    @POST("main/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): TokenResponse

    @POST("main/auth/refresh")
    suspend fun refresh(
        @Body request: RefreshRequest
    ): TokenResponse

    @POST("main/auth/logout")
    suspend fun logout(): Response<Unit>

    @POST("main/auth/logout-all")
    suspend fun logoutAll(): Response<Unit>

    @POST("main/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<Unit>

    @POST("main/auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<Unit>

    @POST("main/auth/google")
    suspend fun googleLogin(
        @Body request: GoogleLoginRequest
    ): TokenResponse

    @POST("main/auth/verify-email")
    suspend fun verifyEmail(
        @Body request: VerifyEmailRequest
    ): SimpleResponse

    @POST("main/auth/resend-verification")
    suspend fun resendVerification(
        @Body request: ResendVerificationRequest
    ): SimpleResponse
}