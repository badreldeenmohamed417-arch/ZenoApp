package com.example.zeno.core.sections.auth.signupSections

import android.util.Patterns
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
import com.example.zeno.core.theme.ButtonFun
import com.example.zeno.core.theme.TextFieldFun
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors
import kotlinx.coroutines.delay

@Composable
fun SignupMiddleSection(
    signup: (String, String) -> Unit,
    errorFun: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isErrorEmail by remember { mutableStateOf(false) }
    var isErrorPassword by remember { mutableStateOf(false) }
    var isErrorConfirmPassword by remember { mutableStateOf(false) }

    val fillFields = errorsTexts("fill_all_fields")
    val invalidEmail = errorsTexts("invalid_email")

    val passwordTooShort = errorsTexts("password_too_short")
    val passwordMissingUppercase = errorsTexts("password_missing_uppercase")
    val passwordMissingLowercase = errorsTexts("password_missing_lowercase")
    val passwordMissingNumber = errorsTexts("password_missing_number")
    val passwordMissingSpecialCharacter = errorsTexts("password_missing_special_character")
    val passwordsDoNotMatch = errorsTexts("passwords_do_not_match")

    val validatePasswordVar = validatePassword(
        password = password,
        password_too_short = passwordTooShort,
        password_missing_uppercase = passwordMissingUppercase,
        password_missing_lowercase = passwordMissingLowercase,
        password_missing_number = passwordMissingNumber,
        password_missing_special_character = passwordMissingSpecialCharacter
    )

    // إعادة ضبط الأخطاء تلقائياً بعد 3 ثوانٍ
    LaunchedEffect(isErrorEmail, isErrorPassword, isErrorConfirmPassword) {
        if (isErrorEmail || isErrorPassword || isErrorConfirmPassword) {
            delay(3000)
            isErrorEmail = false
            isErrorPassword = false
            isErrorConfirmPassword = false
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

            TextFieldFun(
                text = confirmPassword,
                placeholder = "••••••••",
                label = txt("ConfirmPasswordLabel"),
                isError = isErrorConfirmPassword,
                isPassword = true,
                onTextChange = { newText ->
                    confirmPassword = newText
                    if (isErrorConfirmPassword) isErrorConfirmPassword = false
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            ButtonFun(
                onClick = {
                    if (password.isBlank() || email.isBlank() || confirmPassword.isBlank()) {
                        isErrorEmail = true
                        isErrorPassword = true
                        isErrorConfirmPassword = true
                        errorFun(fillFields)
                    } else if (!isValidEmail(email)) {
                        isErrorEmail = true
                        errorFun(invalidEmail)
                    } else if (password != confirmPassword) {
                        isErrorPassword = true
                        isErrorConfirmPassword = true
                        errorFun(passwordsDoNotMatch)
                    } else if (validatePasswordVar != null) {
                        isErrorPassword = true
                        errorFun(validatePasswordVar)
                    } else {
                        isErrorEmail = false
                        isErrorPassword = false
                        isErrorConfirmPassword = false
                        signup(email, password)
                    }
                },
                items = {
                    Text(
                        text = txt("createAccountButton"),
                        color = AppColors.Surface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    }
}

fun validatePassword(
    password: String,
    password_too_short: String,
    password_missing_uppercase: String,
    password_missing_lowercase: String,
    password_missing_number: String,
    password_missing_special_character: String
): String? {
    return when {
        password.length < 8 -> password_too_short
        !password.any { it.isUpperCase() } -> password_missing_uppercase
        !password.any { it.isLowerCase() } -> password_missing_lowercase
        !password.any { it.isDigit() } -> password_missing_number
        !password.any { !it.isLetterOrDigit() } -> password_missing_special_character
        else -> null
    }
}

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS
        .matcher(email)
        .matches()
}