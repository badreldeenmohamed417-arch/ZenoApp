package com.example.zeno.core.sections.auth.signupSections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.core.AuthFooterFun
import com.example.zeno.core.theme.ButtonFun
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors

@Composable
fun SignupBottomSection(
    googleLogin: () -> Unit,
    login: () -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
//            ButtonFun(
//                onClick = {
//                    googleLogin()
//                },
//                items = {
//                    Text(
//                        text = txt("googleLoginButton"),
//                        color = AppColors.Black,
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.SemiBold
//                    )
//                },
//                backgroundColor = AppColors.SurfaceVariant,
//            )
//
//            Spacer(modifier = Modifier.height(30.dp))

            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                AuthFooterFun(
                    text = txt("alreadyHaveAccount"),
                    actionText = txt("loginButton"),
                    onActionClick = {
                        login()
                    }
                )
            }
        }
    }
}