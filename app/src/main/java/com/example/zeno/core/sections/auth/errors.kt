package com.example.zeno.core.sections.auth

import androidx.compose.runtime.Composable
import com.example.zeno.core.txt

@Composable
fun errorsTexts(key: String): String {
    val errors = mapOf(
        "fill_all_fields" to txt("fill_all_fields"),
        "invalid_email" to txt("invalid_email"),
        "invalid_email_or_password" to txt("invalid_email_or_password"),
        "password_too_short" to txt("password_too_short"),
        "password_missing_uppercase" to txt("password_missing_uppercase"),
        "password_missing_lowercase" to txt("password_missing_lowercase"),
        "password_missing_number" to txt("password_missing_number"),
        "password_missing_special_character" to txt("password_missing_special_character"),
        "passwords_do_not_match" to txt("passwords_do_not_match")
    )
    return errors[key] ?: ""
}