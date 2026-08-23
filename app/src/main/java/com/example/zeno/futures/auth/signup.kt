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
import com.example.zeno.core.sections.auth.signupSections.SignupBottomSection
import com.example.zeno.core.sections.auth.signupSections.SignupMiddleSection
import com.example.zeno.core.sections.auth.signupSections.SignupTopSection
import com.example.zeno.data.repository.AuthRepository
import com.example.zeno.futures.MessageType
import com.example.zeno.futures.TopMessage
import com.example.zeno.futures.googleLogin
import com.example.zeno.futures.signUp

@Composable
fun SignupScreen(
    onContinue: () -> Unit,
    login: () -> Unit,
    setup: () -> Unit,
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
            SignupTopSection(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(30.dp))

            SignupMiddleSection(
                signup = { email, password ->
                    signUp(
                        authRepository = authRepository,
                        email = email,
                        password = password,
                        onContinue = setup,
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

            SignupBottomSection(
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
                login = login
            )
        }

        // إظهار الرسالة كـ Overlay أعلى الشاشة بدون حجز مساحة من הـ Column
        TopMessage(
            visible = error,
            message = errorMessage,
            type = MessageType.ERROR,
            onDismiss = { error = false }
        )
    }
}