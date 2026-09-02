package com.example.zeno.features.social.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeno.features.social.data.dto.FriendDTO
import com.example.zeno.features.social.data.dto.FriendRequestDTO
import com.example.zeno.features.social.data.repository.FriendsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class FriendsUiState {
    object Loading : FriendsUiState()
    data class Success(val leaderboard: List<FriendDTO>, val requests: List<FriendRequestDTO>) : FriendsUiState()
    data class Error(val message: String) : FriendsUiState()
}

class FriendsViewModel(private val repository: FriendsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<FriendsUiState>(FriendsUiState.Loading)
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = FriendsUiState.Loading
            val leaderboardResult = repository.getLeaderboard()
            val requestsResult = repository.getFriendRequests()

            if (leaderboardResult.isSuccess && requestsResult.isSuccess) {
                _uiState.value = FriendsUiState.Success(
                    leaderboard = leaderboardResult.getOrNull()?.friends ?: emptyList(),
                    requests = requestsResult.getOrNull()?.requests ?: emptyList()
                )
            } else {
                _uiState.value = FriendsUiState.Error("Failed to load friends data")
            }
        }
    }

    fun acceptRequest(requestId: String) {
        viewModelScope.launch {
            repository.acceptRequest(requestId)
            loadData() // Refresh list
        }
    }
}
