package com.example.zeno.features.social.data.dto

import com.google.gson.annotations.SerializedName

data class FriendDTO(
    val id: String,
    @SerializedName("display_name") val displayName: String,
    val xp: Int,
    val streak: Int,
    val level: Int,
    val rank: Int
)

data class FriendRequestDTO(
    val id: String,
    @SerializedName("from_user_id") val fromUserId: String,
    @SerializedName("from_user_name") val fromUserName: String,
    val status: String
)

data class LeaderboardResponse(
    val friends: List<FriendDTO>
)

data class FriendRequestsResponse(
    val requests: List<FriendRequestDTO>
)

data class ActionResponse(
    val success: Boolean,
    val message: String
)

data class SendFriendRequest(
    @SerializedName("to_user_id") val toUserId: String
)
