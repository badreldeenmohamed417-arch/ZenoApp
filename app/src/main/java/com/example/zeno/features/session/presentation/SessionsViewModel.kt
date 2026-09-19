package com.example.zeno.features.session.presentation

import androidx.lifecycle.ViewModel
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

class SessionsViewModel(
    private val planRepository: StudyPlanRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<SessionsUiState>(SessionsUiState.Loading)
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()

    init {
        loadStudyPlan()
    }

    fun loadStudyPlan() {
        viewModelScope.launch {
            _uiState.value = SessionsUiState.Loading
            val result = planRepository.getCurrentStudyPlan()
            if (result.isSuccess) {
                val plan = result.getOrThrow()
                // Auto update if plan is older than 7 days
                val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                var daysSinceStart = 0L
                try {
                    val startDate = dateFormat.parse(plan.startDate) ?: java.util.Date()
                    val now = java.util.Date()
                    daysSinceStart = maxOf(0L, (now.time - startDate.time) / (1000 * 60 * 60 * 24))
                } catch (e: Exception) { e.printStackTrace() }
                
                if (daysSinceStart >= 7) {
                    generateStudyPlan(180) // Regenerate based on latest analytics
                } else {
                    _uiState.value = SessionsUiState.Success(plan)
                }
            } else {
                _uiState.value = SessionsUiState.Error(result.exceptionOrNull().getUserFriendlyMessage())
            }
        }
    }

    fun generateStudyPlan(minutes: Int) {
        viewModelScope.launch {
            _uiState.value = SessionsUiState.Loading
            val result = planRepository.generateStudyPlan(minutes)
            if (result.isSuccess) {
                _uiState.value = SessionsUiState.Success(result.getOrThrow())
            } else {
                // If offline, try to fallback to current cached plan
                val cached = planRepository.getCurrentStudyPlan()
                if (cached.isSuccess) {
                    _uiState.value = SessionsUiState.Success(cached.getOrThrow())
                } else {
                    _uiState.value = SessionsUiState.Error(result.exceptionOrNull().getUserFriendlyMessage())
                }
            }
        }
    }

    fun startSession(subjectId: String, onStarted: (String) -> Unit) {
        viewModelScope.launch {
            val result = sessionRepository.startSession(subjectId)
            if (result.isSuccess) {
                result.getOrNull()?.sessionId?.let { onStarted(it) }
            }
        }
    }

    fun completeSession(sessionId: String) {
        viewModelScope.launch {
            sessionRepository.completeSession(sessionId)
        }
    }
}
