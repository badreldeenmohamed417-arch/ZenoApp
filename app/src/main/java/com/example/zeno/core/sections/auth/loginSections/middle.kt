package com.example.zeno.core.sections.auth.loginSections

import android.util.Patterns
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.core.sections.auth.errorsTexts
import com.example.zeno.core.theme.ButtonFun
import com.example.zeno.core.theme.TextFieldFun
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors
import kotlinx.coroutines.delay

@Composable
fun LoginMiddleSection(
    forgotPassword: (String) -> Unit,
    login: (String, String) -> Unit,
    errorFun: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isErrorEmail by remember { mutableStateOf(false) }
    var isErrorPassword by remember { mutableStateOf(false) }

    val fillFields = errorsTexts("fill_all_fields")
    val invalidEmail = errorsTexts("invalid_email")
    val invalidEmailOrPassword = errorsTexts("invalid_email_or_password")

    // إعادة ضبط الأخطاء تلقائياً بعد 3 ثوانٍ
    LaunchedEffect(isErrorEmail, isErrorPassword) {
        if (isErrorEmail || isErrorPassword) {
            delay(3000)
            isErrorEmail = false
            isErrorPassword = false
        }
    }

    Box {
        Column {
            TextFieldFun(
                text = email,
                placeholder = "email@example.com",
                label = txt("emailLabel"),
                isError = isErrorEmail,
                onTextChange = { newText ->
                    email = newText
                    if (isErrorEmail) isErrorEmail = false
                }
            )

            TextFieldFun(
                text = password,
                placeholder = "••••••••",
                label = txt("passwordLabel"),
                isError = isErrorPassword,
                isPassword = true,
                onTextChange = { newText ->
                    password = newText
                    if (isErrorPassword) isErrorPassword = false
                }
            )

            TextButton(
                onClick = { forgotPassword(email) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = txt("forgotPassword"),
                    color = AppColors.Accent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ButtonFun(
                onClick = {
                    if (password.isBlank() || email.isBlank()) {
                        isErrorEmail = true
                        isErrorPassword = true
                        errorFun(fillFields)
                    } else if (!isValidEmail(email)) {
                        isErrorEmail = true
                        errorFun(invalidEmail)
                    } else if (!validatePassword(password)) {
                        isErrorEmail = true
                        isErrorPassword = true
                        errorFun(invalidEmailOrPassword)
                    } else {
                        isErrorEmail = false
                        isErrorPassword = false
                        login(email, password)
                    }
                },
                items = {
                    Text(
                        text = txt("loginButton"),
                        color = AppColors.Surface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    }
}

fun validatePassword(password: String): Boolean {
    return password.length >= 8 &&
            password.any { it.isUpperCase() } &&
            password.any { it.isLowerCase() } &&
            password.any { it.isDigit() } &&
            password.any { !it.isLetterOrDigit() }
}

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS
        .matcher(email)
        .matches()
}