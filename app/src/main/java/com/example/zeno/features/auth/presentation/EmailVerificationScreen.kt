package com.example.zeno.features.auth.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmailVerificationScreen(
    onVerified: () -> Unit,
    onResendEmail: () -> Unit,
    onCheckStatus: () -> Unit,
    isChecking: Boolean = false,
    resendCooldownTimer: Int = 0,
    errorMessage: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Email,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "تأكيد البريد الإلكتروني",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "لقد أرسلنا رابط تأكيد إلى بريدك الإلكتروني. يرجى الضغط عليه لتفعيل حسابك، ثم العودة إلى هنا والضغط على 'التالي'.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        Button(
            onClick = onCheckStatus,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isChecking
        ) {
            if (isChecking) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("التالي (تم التأكيد)")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(
            onClick = onResendEmail,
            enabled = resendCooldownTimer == 0 && !isChecking
        ) {
            if (resendCooldownTimer > 0) {
                Text("إعادة الإرسال بعد ${resendCooldownTimer / 60}:${(resendCooldownTimer % 60).toString().padStart(2, '0')}")
            } else {
                Text("إعادة إرسال البريد")
            }
        }
    }
}
