package com.example.zeno.core.sections.auth.mailSent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.R
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors

@Composable
fun MailSentTopSection(
    email: String,
    emailVerification: Boolean,
    modifier: Modifier = Modifier
) {
    // تحديد مفتاح النص بناءً على قيمة emailVerification بشكل مباشر وآمن
    val bodyTextKey = if (emailVerification) "mailSentVerify" else "mailSentReset"

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.zeno_logo),
            contentDescription = "Zeno Logo",
            modifier = Modifier.size(100.dp)
        )

        Text(
            text = "Zeno",
            color = AppColors.TextPrimary,
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = txt("checkYourInbox"),
            color = AppColors.Black,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp)
        )

        Text(
            text = txt(bodyTextKey),
            color = AppColors.Black.copy(alpha = 0.7f),
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp)
        )

        Text(
            text = email,
            color = AppColors.Black,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}