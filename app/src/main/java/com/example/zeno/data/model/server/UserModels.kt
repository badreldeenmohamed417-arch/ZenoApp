package com.example.zeno.data.model.server

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val id: String,
    val email: String,
    val country: String? = null,
    @SerializedName("display_name") val displayName: String? = null,
    val grade: String? = null,
    @SerializedName("school_system") val schoolSystem: String? = null,
    val language: String = "ar",
    @SerializedName("is_verified") val isVerified: Boolean = false,
    @SerializedName("is_active") val isActive: Boolean? = null,
    @SerializedName("is_onboarded") val isOnboarded: Boolean? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    val subjects: List<Subject>? = null
)

data class UpdateUserRequest(
    val display_name: String? = null,
    val grade: String? = null,
    val school_system: String? = null,
    val language: String? = null,
    val country: String? = null
)
