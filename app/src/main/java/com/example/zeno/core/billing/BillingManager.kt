package com.example.zeno.core.billing

import android.content.Context
import android.util.Log
import com.example.zeno.data.local.UserManager
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BillingManager(
    private val context: Context,
    private val userManager: UserManager
) {
    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()

    companion object {
        const val ENTITLEMENT_ID = "zeno_pro"
        const val API_KEY = "test_NXvIUvKBQmqXYtffOzNzdPQVvLf"
    }

    fun initialize() {
        _isPro.value = try {
            userManager.isPro()
        } catch (e: Exception) {
            Log.w("BillingManager", "Could not restore local subscription state", e)
            false
        }

        Purchases.debugLogsEnabled = true
        Purchases.configure(PurchasesConfiguration.Builder(context, API_KEY).build())
        
        Purchases.sharedInstance.updatedCustomerInfoListener = UpdatedCustomerInfoListener { customerInfo ->
            val isProActive = customerInfo.entitlements[ENTITLEMENT_ID]?.isActive == true
            _isPro.value = isProActive
            userManager.saveProStatus(isProActive)
            Log.d("BillingManager", "CustomerInfo updated: isProActive=$isProActive")
        }
    }
}
