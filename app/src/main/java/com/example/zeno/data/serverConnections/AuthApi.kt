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
import com.example.zeno.data.model.server.VerifyEmailRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("auth/complete-data")
    suspend fun completeData(
        @Header("Authorization") token: String,
        @Body request: CompleteDataRequest
    ): Response<Unit>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): TokenResponse

    @POST("auth/refresh")
    suspend fun refresh(
        @Body request: RefreshRequest
    ): TokenResponse

    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("auth/logout-all")
    suspend fun logoutAll(
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<Unit>

    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<Unit>

    @POST("auth/google")
    suspend fun googleLogin(
        @Body request: GoogleLoginRequest
    ): TokenResponse

    @POST("auth/verify-email")
    suspend fun verifyEmail(
        @Body request: VerifyEmailRequest
    ): SimpleResponse

    @POST("auth/resend-verification")
    suspend fun resendVerification(
        @Body request: ResendVerificationRequest
    ): SimpleResponse
}