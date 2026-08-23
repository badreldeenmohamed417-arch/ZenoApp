package com.example.zeno.futures.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.zeno.core.sections.auth.forgotPasswordSections.ForgotPasswordBottomSection
import com.example.zeno.core.sections.auth.forgotPasswordSections.ForgotPasswordMiddleSection
import com.example.zeno.core.sections.auth.forgotPasswordSections.ForgotPasswordTopSection
import com.example.zeno.data.objects.email
import com.example.zeno.data.repository.AuthRepository
import com.example.zeno.futures.MessageType
import com.example.zeno.futures.TopMessage
import com.example.zeno.futures.forgotPassword

@Composable
fun ForgotPasswordScreen(
    onContinue: () -> Unit,
    login: () -> Unit,
) {
    val context = LocalContext.current

    var error by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val authRepository = remember {
        AuthRepository()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ForgotPasswordTopSection(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            ForgotPasswordMiddleSection(
                forgotPassword = { inputEmail ->
                    email.email = inputEmail
                    forgotPassword(
                        authRepository = authRepository,
                        email = inputEmail,
                        onSuccess = {
                            onContinue()
                        },
                        errorFun = { message ->
                            error = true
                            errorMessage = message
                        },
                        disableError = {
                            error = false
                            errorMessage = ""
                        }
                    )
                },
                errorFun = { message ->
                    error = true
                    errorMessage = message
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            ForgotPasswordBottomSection(
                login = login,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        TopMessage(
            visible = error,
            message = errorMessage,
            type = MessageType.ERROR,
            onDismiss = { error = false }
        )
    }
}