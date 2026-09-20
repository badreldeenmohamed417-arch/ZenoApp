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

    private val _currentPlanId = MutableStateFlow<String>("free")
    val currentPlanId: StateFlow<String> = _currentPlanId.asStateFlow()

    private val _currentPlanName = MutableStateFlow<String>("مجانية")
    val currentPlanName: StateFlow<String> = _currentPlanName.asStateFlow()

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

            try {
                val sub = repository.getMySubscription()
                val planName = sub.currentPlan?.lowercase() ?: "free"
                val matchedPlan = _plans.value.find {
                    it.id.equals(planName, ignoreCase = true) ||
                    it.name.en.equals(planName, ignoreCase = true) ||
                    it.name.ar.equals(planName, ignoreCase = true) ||
                    planName.contains(it.id, ignoreCase = true)
                }
                _currentPlanId.value = matchedPlan?.id ?: (if (sub.status == "active" && planName != "free") planName else "free")
                _currentPlanName.value = matchedPlan?.tierTitle?.ar ?: matchedPlan?.name?.ar ?: sub.currentPlan ?: "طالب مجتهد"
            } catch (e: Exception) {
                _currentPlanId.value = "free"
                _currentPlanName.value = "طالب مجتهد"
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
