package com.example.zeno.data.serverConnections

import com.example.zeno.data.model.server.RequestEmailChangeRequest
import com.example.zeno.data.model.server.UpdateUserRequest
import com.example.zeno.data.model.server.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserApi {

    @GET("main/users/me")
    suspend fun getMe(): UserResponse

    @PATCH("main/users/me")
    suspend fun updateMe(
        @Body request: UpdateUserRequest
    ): UserResponse

    @POST("main/users/me/request-email-change")
    suspend fun requestEmailChange(
        @Body request: RequestEmailChangeRequest
    ): Any
}
