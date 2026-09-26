package com.example.zeno.features.premium.presentation

import android.app.Activity
import android.app.Application
import com.example.zeno.core.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeno.features.premium.data.dto.PlanDto
import com.example.zeno.features.premium.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.zeno.core.util.getUserFriendlyMessage
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.interfaces.ReceiveOfferingsCallback
import com.revenuecat.purchases.models.StoreTransaction

class PremiumViewModel(application: Application, private val repository: SubscriptionRepository) : BaseViewModel(application) {
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
        viewModelScope.launch(exceptionHandler) {
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
        viewModelScope.launch(exceptionHandler) {
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

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun purchasePlan(activity: Activity, planId: String) {
        _isLoading.value = true
        _errorMessage.value = null

        Purchases.sharedInstance.getOfferings(object : ReceiveOfferingsCallback {
            override fun onReceived(offerings: Offerings) {
                val allPackages = offerings.all.values.flatMap { it.availablePackages }
                val currentPackages = offerings.current?.availablePackages ?: emptyList()

                // Smart package lookup: by identifier, product id, offering, or package type
                val packageToBuy = 
                    currentPackages.find { it.identifier.equals(planId, ignoreCase = true) }
                    ?: currentPackages.find { it.product.id.equals(planId, ignoreCase = true) }
                    ?: allPackages.find { it.identifier.equals(planId, ignoreCase = true) }
                    ?: allPackages.find { it.product.id.equals(planId, ignoreCase = true) }
                    ?: offerings[planId]?.availablePackages?.firstOrNull()
                    ?: allPackages.find { it.packageType.name.equals(planId, ignoreCase = true) }
                    ?: currentPackages.find { 
                        it.identifier.contains(planId, ignoreCase = true) || 
                        it.product.id.contains(planId, ignoreCase = true) ||
                        planId.contains(it.identifier, ignoreCase = true)
                    }
                    ?: currentPackages.firstOrNull()
                    ?: allPackages.firstOrNull()

                if (packageToBuy != null) {
                    Purchases.sharedInstance.purchase(
                        PurchaseParams.Builder(activity, packageToBuy).build(),
                        object : PurchaseCallback {
                            override fun onCompleted(
                                storeTransaction: StoreTransaction,
                                customerInfo: CustomerInfo
                            ) {
                                _isLoading.value = false
                                _redeemMessage.value = "تم تفعيل الاشتراك بنجاح!"
                                fetchPlans()
                            }

                            override fun onError(
                                error: PurchasesError,
                                userCancelled: Boolean
                            ) {
                                _isLoading.value = false
                                if (!userCancelled) {
                                    _errorMessage.value = "خطأ أثناء الشراء: ${error.message}"
                                }
                            }
                        }
                    )
                } else {
                    _isLoading.value = false
                    _errorMessage.value = "الباقة غير متاحة حالياً للشراء عبر متجر التطبيقات"
                }
            }

            override fun onError(error: PurchasesError) {
                _isLoading.value = false
                _errorMessage.value = "تعذر الاتصال بمتجر التطبيقات: ${error.message}"
            }
        })
    }
}
