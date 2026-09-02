package com.example.zeno.features.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.zeno.R
import com.example.zeno.core.widgets.ZenoButton
import com.example.zeno.core.widgets.ZenoTextField
import com.example.zeno.features.auth.data.AuthRepository
import com.example.zeno.features.auth.data.ForgotPasswordRequest
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    authRepository: AuthRepository,
    onNavigateBackToLogin: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        Image(
            painter = painterResource(id = R.drawable.ic_zeno_logo),
            contentDescription = "Zeno Logo",
            modifier = Modifier.size(64.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = stringResource(id = R.string.auth_forgot_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(id = R.string.auth_forgot_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        ZenoTextField(
            value = email,
            onValueChange = { 
                email = it
                errorMessage = null
                successMessage = null
            },
            placeholder = stringResource(id = R.string.auth_email_hint),
            isError = errorMessage != null,
            enabled = !isLoading
        )
        
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        } else if (successMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = successMessage!!,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        ZenoButton(
            text = stringResource(id = R.string.auth_send_link_button),
            isLoading = isLoading,
            onClick = {
                if (email.isBlank()) {
                    errorMessage = "Please enter your email"
                    return@ZenoButton
                }
                
                isLoading = true
                coroutineScope.launch {
                    val result = authRepository.forgotPassword(ForgotPasswordRequest(email.trim()))
                    isLoading = false
                    if (result.isSuccess) {
                        successMessage = result.getOrNull()?.message ?: "Link sent successfully."
                    } else {
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to send link"
                    }
                }
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        TextButton(
            onClick = onNavigateBackToLogin,
            enabled = !isLoading
        ) {
            Text(
                text = stringResource(id = R.string.auth_back_to_login),
                color = if (isLoading) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
