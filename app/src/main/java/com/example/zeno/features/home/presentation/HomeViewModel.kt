package com.example.zeno.features.home.presentation

import android.app.Application
import com.example.zeno.core.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeno.features.home.data.dto.ProgressOverviewResponse
import com.example.zeno.features.home.data.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.zeno.core.util.getUserFriendlyMessage

import androidx.lifecycle.ViewModel

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val data: ProgressOverviewResponse) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(application: Application, private val repository: ProgressRepository) : BaseViewModel(application) {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch(exceptionHandler) {
            _uiState.value = HomeUiState.Loading
            try {
                val result = repository.getProgressOverview()
                if (result.isSuccess) {
                    _uiState.value = HomeUiState.Success(result.getOrThrow())
                } else {
                    _uiState.value = HomeUiState.Error(
                        result.exceptionOrNull().getUserFriendlyMessage()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.getUserFriendlyMessage())
            }
        }
    }
}

class HomeViewModelFactory(
    private val application: Application,
    private val repository: ProgressRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
