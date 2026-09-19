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
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Current Plan Badge
        Text(
            text = stringResource(R.string.premium_current_plan, stringResource(R.string.premium_column_free)),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = LimeAccent
        )

        Spacer(modifier = Modifier.height(20.dp))

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
                contentPadding = PaddingValues(horizontal = 48.dp),
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
                        isActive = plan.id == "free"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Plan Comparison Table Section (RTL Aligned)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardBG)
                .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Text(
                text = stringResource(R.string.premium_features_table_title),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Table Column Headers (RTL: Right to Left)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.premium_column_feature), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite, modifier = Modifier.weight(1.5f), textAlign = TextAlign.Start)
                Text(text = stringResource(R.string.premium_column_free), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(text = stringResource(R.string.premium_column_classic), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(text = stringResource(R.string.premium_column_pro), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(text = stringResource(R.string.premium_column_plus), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LimeAccent, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CardBorder)

            ComparisonRow(stringResource(R.string.premium_row_credits), "1k", "5k", "15k", "40k")
            ComparisonRow(stringResource(R.string.premium_row_questions), "✓", "✓", "✓", "✓")
            ComparisonRow(stringResource(R.string.premium_row_notes_exams), "-", "✓", "✓", "✓")
            ComparisonRow(stringResource(R.string.premium_row_advanced_ai), "-", "-", "✓", "✓")
            ComparisonRow(stringResource(R.string.premium_row_all_features), "-", "-", "-", "✓")
        }

        Spacer(modifier = Modifier.height(28.dp))

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

    val badgeText = if (isActive) {
        stringResource(R.string.premium_column_free)
    } else {
        if (isEnglish) plan.badge?.en?.ifBlank { plan.badge.ar } else plan.badge?.ar?.ifBlank { plan.badge.en }
    }

    val nameText = if (isEnglish) plan.name.en.ifBlank { plan.name.ar } else plan.name.ar.ifBlank { plan.name.en }
    val priceText = if (isEnglish) plan.price.en.ifBlank { plan.price.ar } else plan.price.ar.ifBlank { plan.price.en }
    val periodText = if (isEnglish) plan.period.en.ifBlank { plan.period.ar } else plan.period.ar.ifBlank { plan.period.en }

    val borderColor = if (isActive) LimeAccent else if (plan.highlighted) LimeAccent else CardBorder
    val borderWidth = if (isActive) 2.5.dp else if (plan.highlighted) 1.5.dp else 1.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBG)
            .border(BorderStroke(borderWidth, borderColor), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (!badgeText.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .background(LimeAccent, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Text(
                text = nameText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Text(
                text = "$priceText $periodText".trim(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (plan.highlighted || isActive) LimeAccent else TextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                plan.features.forEach { feature ->
                    val featureText = if (isEnglish) feature.en.ifBlank { feature.ar } else feature.ar.ifBlank { feature.en }
                    PlanFeatureItem(featureText)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

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

@Composable
fun ComparisonRow(feature: String, free: String, classic: String, pro: String, premium: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = feature, fontSize = 11.sp, color = TextWhite, modifier = Modifier.weight(1.5f), textAlign = TextAlign.Start)
        Text(text = free, fontSize = 11.sp, color = TextMuted, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(text = classic, fontSize = 11.sp, color = TextWhite, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(text = pro, fontSize = 11.sp, color = TextWhite, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(text = premium, fontSize = 11.sp, color = LimeAccent, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
    }
}
