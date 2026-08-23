package com.example.zeno.data.model.server

data class UserResponse(
    val id: String,
    val email: String,
    val country: String,
    val display_name: String?,
    val grade: String?,
    val school_system: String?,
    val language: String,
    val is_verified: Boolean,
    val created_at: String,
    val updated_at: String
)

data class UpdateUserRequest(
    val display_name: String? = null,
    val grade: String? = null,
    val school_system: String? = null,
    val language: String? = null,
    val country: String? = null
)
