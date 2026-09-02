package com.example.zeno.features.social.data.repository

import com.example.zeno.features.social.data.FriendsApi
import com.example.zeno.features.social.data.dto.ActionResponse
import com.example.zeno.features.social.data.dto.FriendRequestsResponse
import com.example.zeno.features.social.data.dto.LeaderboardResponse
import com.example.zeno.features.social.data.dto.SendFriendRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FriendsRepository(private val friendsApi: FriendsApi) {
    suspend fun getLeaderboard(): Result<LeaderboardResponse> = withContext(Dispatchers.IO) {
        try {
            val response = friendsApi.getLeaderboard()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFriendRequests(): Result<FriendRequestsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = friendsApi.getFriendRequests()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun acceptRequest(id: String): Result<ActionResponse> = withContext(Dispatchers.IO) {
        try {
            val response = friendsApi.acceptFriendRequest(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
