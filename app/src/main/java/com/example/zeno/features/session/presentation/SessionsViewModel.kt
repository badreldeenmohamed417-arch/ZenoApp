package com.example.zeno.features.session.presentation

import android.app.Application
import com.example.zeno.core.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeno.features.session.data.dto.StudyPlanSchema
import com.example.zeno.features.session.data.repository.SessionRepository
import com.example.zeno.features.session.data.repository.StudyPlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.zeno.core.util.getUserFriendlyMessage

sealed class SessionsUiState {
    object Loading : SessionsUiState()
    data class Success(val plan: StudyPlanSchema) : SessionsUiState()
    data class Error(val message: String) : SessionsUiState()
}

class SessionsViewModel(application: Application,
    private val planRepository: StudyPlanRepository,
    private val sessionRepository: SessionRepository
) : BaseViewModel(application) {
    private val _uiState = MutableStateFlow<SessionsUiState>(SessionsUiState.Loading)
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()







    fun startSession(subjectId: String, onStarted: (String) -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            val result = sessionRepository.startSession(subjectId)
            if (result.isSuccess) {
                result.getOrNull()?.sessionId?.let { onStarted(it) }
            }
        }
    }

    fun completeSession(sessionId: String) {
        viewModelScope.launch(exceptionHandler) {
            sessionRepository.completeSession(sessionId)
        }
    }
}
