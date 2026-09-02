package com.example.zeno.features.auth.data

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String
)

data class ForgotPasswordRequest(
    val email: String,
    val language: String = "ar"
)

data class GoogleLoginRequest(
    @SerializedName("id_token") val idToken: String
)

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("is_new_user") val isNewUser: Boolean? = false
)

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val id: String
)

data class BaseResponse(
    val success: Boolean,
    val message: String
)

data class CompleteDataRequest(
    val country: String,
    @SerializedName("school_system") val schoolSystem: String? = null,
    val language: String = "ar",
    val grade: String,
    val track: String? = null,
    @SerializedName("display_name") val displayName: String? = null
)

data class UserResponse(
    val id: String,
    val email: String,
    @SerializedName("display_name") val displayName: String?,
    val grade: String?,
    val track: String?,
    @SerializedName("is_verified") val isVerified: Boolean
)

