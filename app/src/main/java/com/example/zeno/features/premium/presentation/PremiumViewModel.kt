package com.example.zeno.features.premium.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeno.features.premium.data.dto.PlanDto
import com.example.zeno.features.premium.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.zeno.core.util.getUserFriendlyMessage

class PremiumViewModel(private val repository: SubscriptionRepository) : ViewModel() {
    private val _plans = MutableStateFlow<List<PlanDto>>(emptyList())
    val plans: StateFlow<List<PlanDto>> = _plans.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _redeemMessage = MutableStateFlow<String?>(null)
    val redeemMessage: StateFlow<String?> = _redeemMessage.asStateFlow()

    init {
        fetchPlans()
    }

    private fun fetchPlans() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getPlans()
            if (result.isSuccess) {
                _plans.value = result.getOrNull() ?: emptyList()
            } else {
                _errorMessage.value = result.exceptionOrNull().getUserFriendlyMessage()
            }
            _isLoading.value = false
        }
    }

    fun redeemCode(code: String) {
        if (code.isBlank()) return
        viewModelScope.launch {
            val result = repository.redeemCode(code)
            if (result.isSuccess) {
                _redeemMessage.value = result.getOrNull()?.message?.ar ?: "تم التفعيل بنجاح"
                fetchPlans()
            } else {
                _redeemMessage.value = "فشل التفعيل، يرجى التأكد من صحة الكود والمحاولة مرة أخرى"
            }
        }
    }

    fun clearRedeemMessage() {
        _redeemMessage.value = null
    }
}
