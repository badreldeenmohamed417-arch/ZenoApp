package com.example.zeno.features.auth.data

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {
    @POST("main/auth/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @POST("main/auth/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): TokenResponse

    @POST("main/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("main/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): BaseResponse

    @POST("main/auth/complete-data")
    suspend fun completeData(@Body request: CompleteDataRequest): UserResponse

    @POST("main/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): TokenResponse

    @POST("main/auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): BaseResponse

    @POST("main/auth/resend-verification")
    suspend fun resendVerification(@Body request: ResendVerificationRequest): BaseResponse
}
