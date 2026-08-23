package com.example.zeno.core.sections.auth.forgotPasswordSections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.core.sections.auth.errorsTexts
import com.example.zeno.core.sections.auth.loginSections.isValidEmail
import com.example.zeno.core.theme.ButtonFun
import com.example.zeno.core.theme.TextFieldFun
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordMiddleSection(
    forgotPassword: (String) -> Unit,
    errorFun: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isErrorEmail by remember { mutableStateOf(false) }

    val fillFields = errorsTexts("fill_all_fields")
    val invalidEmail = errorsTexts("invalid_email")

    // إعادة ضبط الخطأ بعد 3 ثوانٍ تلقائياً
    LaunchedEffect(isErrorEmail) {
        if (isErrorEmail) {
            delay(3000)
            isErrorEmail = false
        }
    }

    Box {
        Column {
            TextFieldFun(
                text = email,
                placeholder = "example@email.com",
                label = txt("emailLabel"),
                isError = isErrorEmail,
                onTextChange = { newText ->
                    email = newText
                    if (isErrorEmail) isErrorEmail = false
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            ButtonFun(
                onClick = {
                    if (email.isBlank()) {
                        isErrorEmail = true
                        errorFun(fillFields)
                    } else if (!isValidEmail(email)) {
                        isErrorEmail = true
                        errorFun(invalidEmail)
                    } else {
                        isErrorEmail = false
                        forgotPassword(email)
                    }
                },
                items = {
                    Text(
                        text = txt("sendRestLink"),
                        color = AppColors.Surface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    }
}