package com.example.zeno.core.sections.auth.loginSections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.zeno.core.AuthFooterFun
import com.example.zeno.core.txt

@Composable
fun LoginBottomSection(
    googleLogin: () -> Unit,
    signup: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        ButtonFun(
//            onClick = googleLogin,
//            items = {
//                Text(
//                    text = txt("googleLoginButton"),
//                    color = AppColors.Black,
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.SemiBold
//                )
//            },
//            backgroundColor = AppColors.SurfaceVariant
//        )

        Spacer(modifier = Modifier.height(30.dp))

        AuthFooterFun(
            text = txt("doNotHaveAccount"),
            actionText = txt("createAccountButton"),
            onActionClick = {
                signup()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}