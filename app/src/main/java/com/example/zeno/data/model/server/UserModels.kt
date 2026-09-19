package com.example.zeno.data.model.server

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val id: String,
    val email: String,
    val username: String? = null,
    val country: String? = null,
    @SerializedName("display_name") val displayName: String? = null,
    val grade: String? = null,
    @SerializedName("school_system") val schoolSystem: String? = null,
    val track: String? = null,
    val language: String = "ar",
    @SerializedName("auth_provider") val authProvider: String? = "email",
    @SerializedName("is_verified") val isVerified: Boolean = false,
    @SerializedName("is_active") val isActive: Boolean? = null,
    @SerializedName("is_onboarded") val isOnboarded: Boolean? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    val subjects: List<Subject>? = null
)

data class UpdateUserRequest(
    val username: String? = null,
    val display_name: String? = null,
    val grade: String? = null,
    val school_system: String? = null,
    val track: String? = null,
    val language: String? = null,
    val country: String? = null
)

data class RequestEmailChangeRequest(
    @SerializedName("new_email") val newEmail: String
)
