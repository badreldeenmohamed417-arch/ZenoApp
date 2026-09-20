package com.example.zeno.features.premium

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zeno.R
import com.example.zeno.core.ui.modifiers.bounceClickable
import com.example.zeno.core.widgets.ZenoTextField
import com.example.zeno.data.local.UserManager

import com.example.zeno.features.premium.data.dto.PlanDto
import com.example.zeno.features.premium.presentation.PremiumViewModel
import kotlin.math.absoluteValue

import com.example.zeno.data.AppColors

private val DarkBG: Color @Composable get() = AppColors.BG
private val LimeAccent: Color @Composable get() = AppColors.LimeAccent
private val CardBG: Color @Composable get() = AppColors.CardBG
private val CardBorder: Color @Composable get() = AppColors.CardBorder
private val TextWhite: Color @Composable get() = AppColors.TextWhite
private val TextMuted: Color @Composable get() = AppColors.TextMuted

@Composable
fun PremiumScreen(
    viewModel: PremiumViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PremiumViewModel(org.koin.core.context.GlobalContext.get().get<com.example.zeno.features.premium.data.repository.SubscriptionRepository>()) as T
    }),
    onBack: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var couponCode by remember { mutableStateOf("") }
    val context = LocalContext.current

    val plansState by viewModel.plans.collectAsState()
    val currentPlanId by viewModel.currentPlanId.collectAsState()
    val currentPlanName by viewModel.currentPlanName.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val redeemMessage by viewModel.redeemMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBG)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.premium_screen_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.premium_screen_subtitle),
                fontSize = 13.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(LimeAccent.copy(alpha = 0.08f))
                    .border(1.dp, LimeAccent.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.premium_equal_features_note),
                    fontSize = 12.sp,
                    color = LimeAccent,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(color = LimeAccent)
        } else if (plansState.isEmpty()) {
            Text(
                text = stringResource(R.string.premium_screen_title),
                fontSize = 16.sp,
                color = TextWhite
            )
        } else {
            val pagerState = rememberPagerState(pageCount = { plansState.size })

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 40.dp),
                pageSpacing = 16.dp
            ) { page ->
                val plan = plansState[page]
                val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                val scale = lerp(
                    start = 0.85f,
                    stop = 1f,
                    fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                )

                Box(
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        alpha = lerp(
                            start = 0.6f,
                            stop = 1f,
                            fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                        )
                    }
                ) {
                    PlanCard(
                        plan = plan,
                        isActive = plan.id.equals(currentPlanId, ignoreCase = true)
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(20.dp))

        // Coupon Code Discount Section
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.premium_have_coupon),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = couponCode,
                    onValueChange = { couponCode = it },
                    placeholder = {
                        Text(
                            text = "ZENO-XXXX-XXXX",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = TextWhite,
                        fontWeight = FontWeight.Medium
                    ),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LimeAccent,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = CardBG,
                        unfocusedContainerColor = CardBG,
                        cursorColor = LimeAccent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(14.dp))
                        .background(LimeAccent)
                        .bounceClickable {
                            viewModel.redeemCode(couponCode)
                        }
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.premium_redeem_button),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            if (redeemMessage != null) {
                Text(
                    text = redeemMessage ?: "",
                    color = LimeAccent,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Telegram Bot Code Purchase Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0088CC))
                    .bounceClickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://t.me/zeno_eg_bot")
                        )
                        context.startActivity(intent)
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_telegram),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.premium_buy_telegram),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun PlanCard(plan: PlanDto, isActive: Boolean = false) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val isEnglish = userManager.getLanguage() == "en"

    val badgeText = if (isEnglish) plan.badge?.en?.ifBlank { plan.badge.ar } else plan.badge?.ar?.ifBlank { plan.badge.en }
    val nameText = if (isEnglish) plan.name.en.ifBlank { plan.name.ar } else plan.name.ar.ifBlank { plan.name.en }
    val tierTitleText = if (isEnglish) plan.tierTitle?.en?.ifBlank { plan.tierTitle.ar } else plan.tierTitle?.ar?.ifBlank { plan.tierTitle.en }
    val priceText = if (isEnglish) plan.price.en.ifBlank { plan.price.ar } else plan.price.ar.ifBlank { plan.price.en }
    val originalPriceText = if (isEnglish) plan.originalPrice?.en?.ifBlank { plan.originalPrice.ar } else plan.originalPrice?.ar?.ifBlank { plan.originalPrice.en }
    val discountBadgeText = if (isEnglish) plan.discountText?.en?.ifBlank { plan.discountText.ar } else plan.discountText?.ar?.ifBlank { plan.discountText.en }
    val periodText = if (isEnglish) plan.period.en.ifBlank { plan.period.ar } else plan.period.ar.ifBlank { plan.period.en }

    val borderColor = if (isActive) LimeAccent else if (plan.highlighted) LimeAccent else CardBorder
    val borderWidth = if (isActive) 2.5.dp else if (plan.highlighted) 1.5.dp else 1.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(CardBG)
            .border(BorderStroke(borderWidth, borderColor), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.height(22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!badgeText.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .background(LimeAccent, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = nameText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    textAlign = TextAlign.Center
                )

                if (!tierTitleText.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .background(LimeAccent.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                            .border(1.dp, LimeAccent.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.premium_profile_title_label, tierTitleText),
                            color = LimeAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = priceText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (plan.highlighted || isActive) LimeAccent else TextWhite
                    )

                    if (!originalPriceText.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = originalPriceText,
                            fontSize = 12.sp,
                            color = TextMuted,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                    }

                    if (!discountBadgeText.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE53935).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .border(0.5.dp, Color(0xFFE53935), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = discountBadgeText,
                                color = Color(0xFFFF5252),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (periodText.isNotBlank()) {
                    Text(
                        text = periodText,
                        fontSize = 11.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    plan.features.forEach { feature ->
                        val featureText = if (isEnglish) feature.en.ifBlank { feature.ar } else feature.ar.ifBlank { feature.en }
                        PlanFeatureItem(featureText)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isActive || plan.highlighted) LimeAccent else CardBorder)
                    .bounceClickable { },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isActive) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = if (isActive) stringResource(R.string.premium_plan_active) else stringResource(R.string.premium_select_plan),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive || plan.highlighted) Color.Black else TextWhite
                    )
                }
            }
        }
    }
}

@Composable
fun PlanFeatureItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 3.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = LimeAccent,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = TextWhite
        )
    }
}

