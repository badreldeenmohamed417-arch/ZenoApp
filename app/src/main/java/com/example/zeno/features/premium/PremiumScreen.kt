package com.example.zeno.futures.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors
import com.example.zeno.futures.session.Orb

@Composable
fun PremiumScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BG)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 24.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(34.dp).background(AppColors.SurfaceVariant, CircleShape)
        ) {
            Icon(Icons.Default.Close, contentDescription = null, tint = AppColors.TextMuted, modifier = Modifier.size(14.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Orb(modifier = Modifier.size(64.dp), isGold = true)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = txt("premiumTitle"),
                fontSize = 23.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BenefitRow(txt("premiumBenefit1"))
            BenefitRow(txt("premiumBenefit2"))
            BenefitRow(txt("premiumBenefit3"))
            BenefitRow(txt("premiumBenefit4"))
        }

        Spacer(modifier = Modifier.height(22.dp))

        PriceCard()

        Spacer(modifier = Modifier.height(6.dp))

        Button(
            onClick = { /* Subscribe logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(AppColors.Gold, Color(0xFFC6842E))), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(txt("premiumSubscribeButton"), color = Color(0xFF1A1200), fontWeight = FontWeight.Bold)
            }
        }

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(txt("premiumNotNow"), color = AppColors.TextMuted, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun BenefitRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.Surface)
            .border(1.dp, AppColors.UnfocusedBorder, RoundedCornerShape(12.dp))
            .padding(13.dp, 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(22.dp).clip(CircleShape).background(AppColors.GoldSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = AppColors.Gold, modifier = Modifier.size(12.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun PriceCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(listOf(AppColors.SurfaceVariant, AppColors.Surface)))
            .border(1.5.dp, AppColors.Gold, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = "99", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "ج.م / شهريًا", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextMuted)
            }
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AppColors.Gold)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(text = txt("premiumMostPopular"), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A1200))
            }
        }
    }
}
