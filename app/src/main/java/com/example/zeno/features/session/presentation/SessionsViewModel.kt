package com.example.zeno.features.session.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeno.features.session.data.dto.StudySessionDTO
import com.example.zeno.features.session.data.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SessionsUiState {
    object Loading : SessionsUiState()
    data class Success(val sessions: List<StudySessionDTO>) : SessionsUiState()
    data class Error(val message: String) : SessionsUiState()
}

class SessionsViewModel(private val repository: SessionRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<SessionsUiState>(SessionsUiState.Loading)
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()

    init {
        loadSessions()
    }

    fun loadSessions() {
        viewModelScope.launch {
            _uiState.value = SessionsUiState.Loading
            val result = repository.getSessions()
            if (result.isSuccess) {
                _uiState.value = SessionsUiState.Success(result.getOrNull()?.sessions ?: emptyList())
            } else {
                _uiState.value = SessionsUiState.Error(result.exceptionOrNull()?.message ?: "Failed to load sessions")
            }
        }
    }
}
