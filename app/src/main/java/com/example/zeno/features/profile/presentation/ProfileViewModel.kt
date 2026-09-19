package com.example.zeno.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeno.features.student.data.dto.DeleteAccountRequest
import com.example.zeno.features.student.data.dto.ProfileResponse
import com.example.zeno.features.student.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val data: ProfileResponse) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

fun getErrorMessage(e: Throwable?): String {
    if (e == null) return "Unknown error"
    val msg = e.message ?: "Unknown error"
    if (msg.contains("Unable to resolve host") || msg.contains("timeout") || msg.contains("Failed to connect")) {
        return "لا يوجد اتصال بالإنترنت"
    }
    return msg
}

class ProfileViewModel(private val repository: StudentRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val profileResult = repository.getProfile()
                val dashboardResult = repository.getDashboard()
                
                if (profileResult.isSuccess && dashboardResult.isSuccess) {
                    val profile = profileResult.getOrThrow()
                    val dashboard = dashboardResult.getOrThrow()
                    
                    val combinedProfile = profile.copy(
                        xp = dashboard.student.xp,
                        streak = dashboard.student.streak,
                        level = dashboard.student.level
                    )
                    _uiState.value = ProfileUiState.Success(combinedProfile)
                } else {
                    val errorMsg = getErrorMessage(profileResult.exceptionOrNull() ?: dashboardResult.exceptionOrNull())
                    _uiState.value = ProfileUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(getErrorMessage(e))
            }
        }
    }

    private val _settingsState = MutableStateFlow<com.example.zeno.features.student.data.dto.SettingsResponse?>(null)
    val settingsState: StateFlow<com.example.zeno.features.student.data.dto.SettingsResponse?> = _settingsState.asStateFlow()

    private val _notificationsState = MutableStateFlow<com.example.zeno.features.student.data.dto.NotificationPreferencesResponse?>(null)
    val notificationsState: StateFlow<com.example.zeno.features.student.data.dto.NotificationPreferencesResponse?> = _notificationsState.asStateFlow()

    fun loadSettings() {
        viewModelScope.launch {
            val result = repository.getSettings()
            if (result.isSuccess) {
                _settingsState.value = result.getOrNull()
            }
        }
    }

    fun updateSettings(language: String? = null, theme: String? = null) {
        viewModelScope.launch {
            val request = com.example.zeno.features.student.data.dto.UpdateSettingsRequest(language, theme)
            val result = repository.updateSettings(request)
            if (result.isSuccess) {
                loadSettings() // Reload
            }
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            val result = repository.getNotificationPreferences()
            if (result.isSuccess) {
                _notificationsState.value = result.getOrNull()
            }
        }
    }

    fun updateNotifications(request: com.example.zeno.features.student.data.dto.UpdateNotificationPreferencesRequest) {
        viewModelScope.launch {
            val result = repository.updateNotificationPreferences(request)
            if (result.isSuccess) {
                loadNotifications()
            }
        }
    }

    fun changePassword(current: String, newPass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val request = com.example.zeno.features.student.data.dto.UpdateCredentialsRequest(currentPassword = current, newPassword = newPass)
            val result = repository.updateCredentials(request)
            if (result.isSuccess) onResult(true, "auto_str_تم_تحديث_كلمة")
            else onResult(false, getErrorMessage(result.exceptionOrNull()))
        }
    }

    fun updateUsername(newUsername: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateProfile(mapOf("username" to newUsername))
            if (result.isSuccess) {
                loadProfile()
                onResult(true, "Username updated")
            } else {
                val error = getErrorMessage(result.exceptionOrNull())
                onResult(false, if (error.contains("already taken", ignoreCase = true)) "profile_username_taken" else error)
            }
        }
    }

    fun updateDisplayName(newName: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateProfile(mapOf("display_name" to newName))
            if (result.isSuccess) {
                loadProfile()
                onResult(true, "Name updated")
            } else {
                onResult(false, getErrorMessage(result.exceptionOrNull()))
            }
        }
    }

    fun requestEmailChange(newEmail: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.requestEmailChange(newEmail)
            if (result.isSuccess) {
                onResult(true, "profile_email_verification_sent")
            } else {
                onResult(false, getErrorMessage(result.exceptionOrNull()))
            }
        }
    }

    fun requestDataExport(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.requestDataExport()
            if (result.isSuccess) {
                onResult(true, result.getOrDefault("account_export_request_sent"))
            } else {
                onResult(false, getErrorMessage(result.exceptionOrNull()))
            }
        }
    }

    fun deleteConversations(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteConversations()
            if (result.isSuccess) onResult(true, "auto_str_تم_حذف_جميع")
            else onResult(false, "حدث خطأ أثناء الحذف")
        }
    }

    fun deleteAccount(password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteAccount(DeleteAccountRequest(password = password))
            if (result.isSuccess) onResult(true, "auto_str_تم_حذف_الحساب")
            else onResult(false, getErrorMessage(result.exceptionOrNull()))
        }
    }

    fun deleteAccountGoogle(googleIdToken: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteAccount(DeleteAccountRequest(googleIdToken = googleIdToken))
            if (result.isSuccess) onResult(true, "auto_str_تم_حذف_الحساب")
            else onResult(false, getErrorMessage(result.exceptionOrNull()))
        }
    }
}
