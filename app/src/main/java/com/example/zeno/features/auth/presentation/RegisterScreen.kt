package com.example.zeno.features.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.zeno.core.NetworkUtils
import com.example.zeno.features.auth.data.AuthRepository
import com.example.zeno.features.auth.data.LoginRequest
import com.example.zeno.features.auth.data.RegisterRequest
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    authRepository: AuthRepository,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onGoogleSignIn: (onComplete: () -> Unit) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isGoogleLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val requiredFieldsMsg = stringResource(R.string.setup_username_required_error)
    val usernameTakenError = stringResource(R.string.setup_username_taken_error)
    val scrollState = rememberScrollState()

    val isAnyLoading = isLoading || isGoogleLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        Image(
            painter = painterResource(id = R.drawable.ic_zeno_logo),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = stringResource(id = R.string.auth_register_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(id = R.string.auth_register_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        ZenoTextField(
            value = name,
            onValueChange = { name = it; errorMessage = null },
            placeholder = stringResource(id = R.string.auth_name_hint),
            isError = errorMessage != null,
            enabled = !isAnyLoading
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        ZenoTextField(
            value = username,
            onValueChange = { username = it; errorMessage = null },
            placeholder = stringResource(id = R.string.setup_username_hint),
            isError = errorMessage != null,
            enabled = !isAnyLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        ZenoTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            placeholder = stringResource(id = R.string.auth_email_hint),
            isError = errorMessage != null,
            enabled = !isAnyLoading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ZenoTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            placeholder = stringResource(id = R.string.auth_password_hint),
            isError = errorMessage != null,
            isPassword = true,
            enabled = !isAnyLoading
        )
        
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(28.dp))
        }

        ZenoButton(
            text = stringResource(id = R.string.auth_register_button),
            isLoading = isLoading,
            enabled = !isAnyLoading,
            onClick = {
                if (name.isBlank() || username.isBlank() || email.isBlank() || password.isBlank()) {
                    errorMessage = requiredFieldsMsg
                    return@ZenoButton
                }
                if (username.trim().length < 2 || username.trim().length > 15) {
                    errorMessage = "اسم المستخدم يجب أن يكون بين 2 و 15 حرفاً"
                    return@ZenoButton
                }
                
                isLoading = true
                coroutineScope.launch {
                    val registerResult = authRepository.register(
                        RegisterRequest(
                            email = email.trim(),
                            password = password,
                            username = username.trim(),
                            displayName = name.trim()
                        )
                    )
                    if (registerResult.isSuccess) {
                        // Immediately login to acquire access token
                        val loginResult = authRepository.login(
                            LoginRequest(
                                email = email.trim(),
                                password = password
                            )
                        )
                        isLoading = false
                        if (loginResult.isSuccess) {
                            onRegisterSuccess()
                        } else {
                            errorMessage = loginResult.exceptionOrNull()?.let { 
                                NetworkUtils.getErrorMessage(it) 
                            } ?: "Login failed after registration"
                        }
                    } else {
                        isLoading = false
                        val exc = registerResult.exceptionOrNull()
                        val msg = exc?.let { NetworkUtils.getErrorMessage(it) } ?: ""
                        errorMessage = if (msg.contains("taken", ignoreCase = true) || msg.contains("username", ignoreCase = true) || msg.contains("مستخدم", ignoreCase = true)) {
                            usernameTakenError
                        } else {
                            msg.ifBlank { "Registration failed" }
                        }
                    }
                }
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = stringResource(id = R.string.auth_has_account),
            color = if (isAnyLoading) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.clickable(enabled = !isAnyLoading) { onNavigateToLogin() }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
            Text(
                text = stringResource(id = R.string.auth_or_login_with),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.labelMedium
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedButton(
            onClick = {
                if (!isAnyLoading) {
                    isGoogleLoading = true
                    onGoogleSignIn {
                        isGoogleLoading = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isAnyLoading,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onBackground,
                disabledContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
            )
        ) {
            if (isGoogleLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(id = R.string.auth_login_google),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}
