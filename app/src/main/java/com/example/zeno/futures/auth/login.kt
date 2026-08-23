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
import com.example.zeno.core.OrDivider
import com.example.zeno.core.sections.auth.loginSections.LoginBottomSection
import com.example.zeno.core.sections.auth.loginSections.LoginMiddleSection
import com.example.zeno.core.sections.auth.loginSections.LoginTopSection
import com.example.zeno.data.objects.email
import com.example.zeno.data.repository.AuthRepository
import com.example.zeno.futures.TopMessage
import com.example.zeno.futures.googleLogin
import com.example.zeno.futures.login


@Composable
fun LoginScreen(
    onContinue: () -> Unit,
    forgotPassword: () -> Unit,
    register: () -> Unit,
    setup: () -> Unit,
) {
    val context = LocalContext.current

    var error by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val authRepository = remember { AuthRepository() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LoginTopSection(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            LoginMiddleSection(
                forgotPassword = { Email ->
                    email.email = Email
                    forgotPassword()
                },
                login = { Email, password ->
                    email.email = Email
                    login(
                        authRepository = authRepository,
                        email = Email,
                        password = password,
                        onContinue = onContinue,
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

            Spacer(modifier = Modifier.height(30.dp))

            OrDivider()

            Spacer(modifier = Modifier.height(30.dp))

            LoginBottomSection(
                googleLogin = {
                    googleLogin(
                        context = context,
                        authRepository = authRepository,
                        onContinue = { isNewUser ->
                            if (isNewUser) {
                                setup()
                            } else {
                                onContinue()
                            }
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
                signup = register,
            )
        }

        // الرسالة طافية فوق كل العناصر (Overlay) بدون أخذ مساحة من الـ Column
        TopMessage(
            visible = error,
            message = errorMessage,
            type = com.example.zeno.futures.MessageType.ERROR,
            onDismiss = { error = false }
        )
    }
}

