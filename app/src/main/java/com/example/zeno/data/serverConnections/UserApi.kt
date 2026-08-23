package com.example.zeno.data.serverConnections

import com.example.zeno.data.model.server.UpdateUserRequest
import com.example.zeno.data.model.server.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserApi {

    @GET("users/me")
    suspend fun getMe(): UserResponse

    @PATCH("users/me")
    suspend fun updateMe(
        @Body request: UpdateUserRequest
    ): UserResponse
}
