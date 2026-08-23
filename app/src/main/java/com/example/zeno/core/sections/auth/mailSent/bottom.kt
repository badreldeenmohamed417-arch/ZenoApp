package com.example.zeno.core.sections.auth.mailSent

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.zeno.core.theme.ButtonFun
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors

@Composable
fun MailSentBottomSection(
    clickPrimary: () -> Unit,
    emailVerification: Boolean,
    clickSecondary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mainTextKey = if (emailVerification) "Continue" else "resendMail"
    val secondaryTextKey = if (emailVerification) "resendMail" else "backToLogin"

    Column(modifier = modifier) {
        ButtonFun(
            onClick = clickPrimary,
            items = {
                Text(
                    text = txt(mainTextKey),
                    color = AppColors.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            backgroundColor = AppColors.Surface.copy(alpha = 0.5f),
        )

        ButtonFun(
            onClick = clickSecondary,
            items = {
                Text(
                    text = txt(secondaryTextKey),
                    color = AppColors.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            backgroundColor = Color.Transparent
        )
    }
}