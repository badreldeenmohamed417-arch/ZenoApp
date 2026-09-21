package com.example.zeno.features.auth.presentation

import android.app.Application
import com.example.zeno.core.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeno.features.auth.data.AuthRepository
import com.example.zeno.features.auth.data.ResendVerificationRequest
import com.example.zeno.features.student.data.repository.StudentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmailVerificationViewModel(application: Application,
    private val authRepository: AuthRepository,
    private val studentRepository: StudentRepository
) : BaseViewModel(application) {

    private val _isChecking = MutableStateFlow(false)
    val isChecking: StateFlow<Boolean> = _isChecking.asStateFlow()

    private val _isVerified = MutableStateFlow(false)
    val isVerified: StateFlow<Boolean> = _isVerified.asStateFlow()

    private val _resendCooldownTimer = MutableStateFlow(0)
    val resendCooldownTimer: StateFlow<Int> = _resendCooldownTimer.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var userEmail: String? = null

    init {
        fetchEmail()
    }

    private fun fetchEmail() {
        viewModelScope.launch(exceptionHandler) {
            val result = studentRepository.getProfile()
            if (result.isSuccess) {
                userEmail = result.getOrNull()?.email
            }
        }
    }

    fun checkVerificationStatus() {
        _isChecking.value = true
        _errorMessage.value = null
        viewModelScope.launch(exceptionHandler) {
            val result = studentRepository.getProfile()
            _isChecking.value = false
            if (result.isSuccess) {
                val profile = result.getOrNull()
                userEmail = profile?.email
                if (profile?.isVerified == true) {
                    _isVerified.value = true
                } else {
                    _errorMessage.value = "لم يتم تفعيل الحساب بعد. يرجى مراجعة بريدك الإلكتروني والضغط على رابط التفعيل."
                }
            } else {
                _errorMessage.value = "فشل في التحقق من الحالة. يرجى المحاولة مرة أخرى."
            }
        }
    }

    fun resendEmail() {
        if (_resendCooldownTimer.value > 0) return
        val email = userEmail
        if (email == null) {
            _errorMessage.value = "جاري تحميل بيانات الحساب، يرجى الانتظار قليلاً..."
            return
        }

        _errorMessage.value = null
        viewModelScope.launch(exceptionHandler) {
            val result = authRepository.resendVerification(ResendVerificationRequest(email))
            if (result.isSuccess) {
                startCooldownTimer(120) // 2 minutes
                _errorMessage.value = "تم إرسال رابط التفعيل مرة أخرى بنجاح."
            } else {
                val exception = result.exceptionOrNull()?.message ?: "حدث خطأ غير متوقع"
                if (exception.contains("429")) {
                    startCooldownTimer(1800) // 30 minutes if too many attempts
                    _errorMessage.value = "تجاوزت الحد الأقصى للمحاولات. يرجى المحاولة بعد 30 دقيقة."
                } else {
                    _errorMessage.value = "فشل في إرسال رابط التفعيل. يرجى المحاولة مرة أخرى."
                }
            }
        }
    }

    private fun startCooldownTimer(seconds: Int) {
        _resendCooldownTimer.value = seconds
        viewModelScope.launch(exceptionHandler) {
            while (_resendCooldownTimer.value > 0) {
                delay(1000)
                _resendCooldownTimer.value -= 1
            }
        }
    }
}
