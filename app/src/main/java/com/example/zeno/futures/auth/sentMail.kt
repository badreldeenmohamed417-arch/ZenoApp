package com.example.zeno.futures.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.zeno.core.sections.auth.mailSent.MailSentBottomSection
import com.example.zeno.core.sections.auth.mailSent.MailSentTopSection
import com.example.zeno.data.local.UserManager
import com.example.zeno.data.objects.email
import com.example.zeno.data.repository.AuthRepository
import com.example.zeno.data.repository.UserRepository
import com.example.zeno.futures.MessageType
import com.example.zeno.futures.TopMessage
import com.example.zeno.futures.checkVerificationStatus
import com.example.zeno.futures.forgotPassword
import com.example.zeno.futures.resendVerification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface MailSentType {
    data object Verification : MailSentType
    data object PasswordReset : MailSentType
}

private data class BannerState(
    val visible: Boolean = false,
    val message: String = "",
    val type: MessageType = MessageType.ERROR
)

@Composable
fun MailSentVerifyScreen(
    onContinue: () -> Unit
) {
    val authRepository = remember { AuthRepository() }
    val userRepository = remember { UserRepository() }
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    var error by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box {
        MailSentContent(
            type = MailSentType.Verification,
            onPrimaryClick = {
                checkVerificationStatus(
                    userRepository = userRepository,
                    userManager = userManager,
                    onVerified = onContinue,
                    onNotVerified = {
                        error = true
                        errorMessage = "لم يتم تفعيل الحساب بعد. يرجى مراجعة بريدك الإلكتروني."
                    },
                    errorFun = { msg ->
                        error = true
                        errorMessage = msg
                    }
                )
            },
            onSecondaryClick = { onSuccess, onError, onDisableError ->
                resendVerification(
                    authRepository = authRepository,
                    email = email.email,
                    onSuccess = { msg ->
                        onSuccess(msg.ifBlank { "تم إعادة إرسال رابط التحقق بنجاح" })
                    },
                    errorFun = onError,
                    disableError = onDisableError
                )
            }
        )

        TopMessage(
            visible = error,
            message = errorMessage,
            type = MessageType.ERROR,
            onDismiss = { error = false }
        )
    }
}

@Composable
fun MailSentResetScreen(
    login: () -> Unit
) {
    val authRepository = remember { AuthRepository() }

    MailSentContent(
        type = MailSentType.PasswordReset,
        onPrimaryClickWithAction = { onSuccess, onError, onDisableError ->
            forgotPassword(
                authRepository = authRepository,
                email = email.email,
                onSuccess = {
                    onSuccess("تم إعادة إرسال رابط تعيين كلمة المرور بنجاح")
                },
                errorFun = onError,
                disableError = onDisableError
            )
        },
        onSecondaryClick = { _, _, _ -> login() }
    )
}

@Composable
private fun MailSentContent(
    type: MailSentType,
    onPrimaryClick: (() -> Unit)? = null,
    onPrimaryClickWithAction: ((onSuccess: (String) -> Unit, onError: (String) -> Unit, onDisableError: () -> Unit) -> Unit)? = null,
    onSecondaryClick: (onSuccess: (String) -> Unit, onError: (String) -> Unit, onDisableError: () -> Unit) -> Unit
) {
    var bannerState by remember { mutableStateOf(BannerState()) }

    val isVerification = type is MailSentType.Verification

    // إخفاء الرسالة تلقائياً بعد 3.5 ثوانٍ
    LaunchedEffect(bannerState.visible) {
        if (bannerState.visible) {
            delay(3500L)
            bannerState = bannerState.copy(visible = false)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopMessage(
            visible = bannerState.visible,
            message = bannerState.message,
            type = bannerState.type
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            MailSentTopSection(
                email = email.email,
                emailVerification = isVerification,
                modifier = Modifier.padding(top = 32.dp)
            )

            MailSentBottomSection(
                emailVerification = isVerification,
                clickPrimary = {
                    bannerState = bannerState.copy(visible = false)
                    if (onPrimaryClick != null) {
                        onPrimaryClick()
                    } else {
                        onPrimaryClickWithAction?.invoke(
                            { successMsg ->
                                bannerState = BannerState(
                                    visible = true,
                                    message = successMsg,
                                    type = MessageType.SUCCESS
                                )
                            },
                            { errorMsg ->
                                bannerState = BannerState(
                                    visible = true,
                                    message = errorMsg,
                                    type = MessageType.ERROR
                                )
                            },
                            { bannerState = bannerState.copy(visible = false) }
                        )
                    }
                },
                clickSecondary = {
                    onSecondaryClick(
                        { successMsg ->
                            bannerState = BannerState(
                                visible = true,
                                message = successMsg,
                                type = MessageType.SUCCESS
                            )
                        },
                        { errorMsg ->
                            bannerState = BannerState(
                                visible = true,
                                message = errorMsg,
                                type = MessageType.ERROR
                            )
                        },
                        { bannerState = bannerState.copy(visible = false) }
                    )
                },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

fun forgotPassword(
    authRepository: AuthRepository,
    email: String,
    onSuccess: () -> Unit,
    errorFun: (String) -> Unit,
    disableError: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            authRepository.forgotPassword(email = email)

            withContext(Dispatchers.Main) {
                disableError()
                onSuccess()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                errorFun(e.message ?: "حدث خطأ أثناء إرسال رابط إعادة التعيين إلى البريد الإلكتروني")
            }
        }
    }
}