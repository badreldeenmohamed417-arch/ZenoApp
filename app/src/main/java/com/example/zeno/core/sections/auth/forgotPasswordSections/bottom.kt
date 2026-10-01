package com.example.zeno.core.sections.auth.forgotPasswordSections

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.zeno.core.AuthFooterFun
import com.example.zeno.core.txt

@Composable
fun ForgotPasswordBottomSection(
    login: () -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier
    ) {
        AuthFooterFun(
            text = txt("rememberedPassword"),
            actionText = txt("loginButton"),
            onActionClick = {
                login()
            }
        )
    }
}