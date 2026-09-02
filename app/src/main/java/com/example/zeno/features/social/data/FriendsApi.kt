package com.example.zeno.features.social.data

import com.example.zeno.features.social.data.dto.ActionResponse
import com.example.zeno.features.social.data.dto.FriendRequestsResponse
import com.example.zeno.features.social.data.dto.LeaderboardResponse
import com.example.zeno.features.social.data.dto.SendFriendRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FriendsApi {
    @GET("main/friends/leaderboard")
    suspend fun getLeaderboard(): LeaderboardResponse

    @GET("main/friends/requests")
    suspend fun getFriendRequests(): FriendRequestsResponse

    @POST("main/friends/requests")
    suspend fun sendFriendRequest(@Body request: SendFriendRequest): ActionResponse

    @POST("main/friends/requests/{id}/accept")
    suspend fun acceptFriendRequest(@Path("id") requestId: String): ActionResponse
}
