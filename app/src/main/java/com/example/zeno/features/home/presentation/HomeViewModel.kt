package com.example.zeno.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeno.features.student.data.dto.StudentDashboardResponse
import com.example.zeno.features.student.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val data: StudentDashboardResponse) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(private val repository: StudentRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val result = repository.getDashboard()
            if (result.isSuccess) {
                _uiState.value = HomeUiState.Success(result.getOrThrow())
            } else {
                _uiState.value = HomeUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
}
