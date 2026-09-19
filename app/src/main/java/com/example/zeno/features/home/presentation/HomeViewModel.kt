package com.example.zeno.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeno.features.home.data.dto.ProgressOverviewResponse
import com.example.zeno.features.home.data.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.zeno.core.util.getUserFriendlyMessage

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val data: ProgressOverviewResponse) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(private val repository: ProgressRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val result = repository.getProgressOverview()
            if (result.isSuccess) {
                _uiState.value = HomeUiState.Success(result.getOrThrow())
            } else {
                _uiState.value = HomeUiState.Error(result.exceptionOrNull().getUserFriendlyMessage())
            }
        }
    }
}

class HomeViewModelFactory(
    private val repository: ProgressRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
