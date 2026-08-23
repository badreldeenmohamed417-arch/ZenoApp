package com.example.zeno.data.repository

import com.example.zeno.data.model.server.UpdateUserRequest
import com.example.zeno.data.model.server.UserResponse
import com.example.zeno.data.server.ApiClient

class UserRepository {

    private val api
        get() = ApiClient.user()

    suspend fun getMe(): UserResponse {
        return api.getMe()
    }

    suspend fun updateMe(
        displayName: String? = null,
        grade: String? = null,
        schoolSystem: String? = null,
        language: String? = null,
        country: String? = null
    ): UserResponse {

        return api.updateMe(
            UpdateUserRequest(
                display_name = displayName,
                grade = grade,
                school_system = schoolSystem,
                language = language,
                country = country
            )
        )
    }
}
