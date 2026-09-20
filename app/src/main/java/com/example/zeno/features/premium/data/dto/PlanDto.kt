package com.example.zeno.features.premium.data.dto

import com.google.gson.annotations.SerializedName

data class LocalizedText(
    val ar: String = "",
    val en: String = ""
)

data class PlanDto(
    val id: String = "",
    val name: LocalizedText = LocalizedText(),
    @SerializedName("tier_title") val tierTitle: LocalizedText? = null,
    val price: LocalizedText = LocalizedText(),
    @SerializedName("original_price") val originalPrice: LocalizedText? = null,
    @SerializedName("discount_text") val discountText: LocalizedText? = null,
    val period: LocalizedText = LocalizedText(),
    @SerializedName("monthly_credits") val monthly_credits: Int = 0,
    val highlighted: Boolean = false,
    val badge: LocalizedText? = null,
    val features: List<LocalizedText> = emptyList()
)

data class RedeemRequest(
    val code: String
)

data class RedeemResponse(
    val success: Boolean = false,
    val message: LocalizedText? = null
)
