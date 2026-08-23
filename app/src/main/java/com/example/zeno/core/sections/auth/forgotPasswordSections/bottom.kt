package com.example.zeno.core.sections.auth.forgotPasswordSections

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.zeno.core.AuthFooterFun
import com.example.zeno.core.theme.ButtonFun
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors

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