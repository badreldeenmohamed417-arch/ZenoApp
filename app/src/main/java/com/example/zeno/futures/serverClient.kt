package com.example.zeno.futures

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.zeno.BuildConfig
import com.example.zeno.data.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ==========================================
// Authentication: Signup & Login
// ==========================================

fun signUp(
    authRepository: AuthRepository,
    email: String,
    password: String,
    onContinue: () -> Unit,
    errorFun: (String) -> Unit,
    disableError: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            authRepository.register(
                email = email,
                password = password
            )

            withContext(Dispatchers.Main) {
                disableError()
                onContinue()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                errorFun(e.message ?: "حدث خطأ أثناء إنشاء الحساب")
            }
        }
    }
}

fun login(
    authRepository: AuthRepository,
    email: String,
    password: String,
    onContinue: () -> Unit,
    errorFun: (String) -> Unit,
    disableError: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            authRepository.login(
                email = email,
                password = password,
                deviceName = "Zeno Android",
                platform = "android"
            )

            withContext(Dispatchers.Main) {
                disableError()
                onContinue()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                errorFun("حدث خطأ أثناء تسجيل الدخول")
            }
        }
    }
}

fun googleLogin(
    context: Context,
    authRepository: AuthRepository,
    onContinue: (isNewUser: Boolean) -> Unit,
    errorFun: (String) -> Unit,
    disableError: () -> Unit
) {
    CoroutineScope(Dispatchers.Main).launch {
        try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = context,
                request = request
            )

            val credential = result.credential
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val googleIdToken = googleCredential.idToken

            // Send Google ID Token to Zeno Server
            val response = withContext(Dispatchers.IO) {
                authRepository.googleLogin(
                    idToken = googleIdToken
                )
            }

            // Zeno login successful
            disableError()
            onContinue(response.isNewUser == true)

        } catch (e: Exception) {
            errorFun("حدث خطأ أثناء تسجيل الدخول باستخدام Google")
        }
    }
}

fun completeUserData(
    authRepository: AuthRepository,
    country: String? = null,
    displayName: String? = null,
    grade: String? = null,
    schoolSystem: String? = null,
    language: String = "ar",
    onContinue: () -> Unit,
    errorFun: (String) -> Unit,
    disableError: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            authRepository.completeData(
                country = country,
                displayName = displayName,
                grade = grade,
                schoolSystem = schoolSystem,
                language = language
            )

            withContext(Dispatchers.Main) {
                disableError()
                onContinue()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                errorFun(e.message ?: "حدث خطأ أثناء حفظ البيانات")
            }
        }
    }
}

// ==========================================
// Email Verification Flow
// ==========================================

fun verifyEmail(
    authRepository: AuthRepository,
    token: String,
    onSuccess: (String) -> Unit,
    errorFun: (String) -> Unit,
    disableError: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = authRepository.verifyEmail(token = token)

            withContext(Dispatchers.Main) {
                disableError()
                onSuccess(response.message)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                errorFun(e.message ?: "رمز التحقق غير صالح أو انتهت صلاحيته")
            }
        }
    }
}

fun resendVerification(
    authRepository: AuthRepository,
    email: String,
    language: String = "ar",
    onSuccess: (String) -> Unit,
    errorFun: (String) -> Unit,
    disableError: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = authRepository.resendVerification(
                email = email,
                language = language
            )

            withContext(Dispatchers.Main) {
                disableError()
                onSuccess(response.message)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                errorFun(e.message ?: "حدث خطأ أثناء إعادة إرسال رابط التحقق")
            }
        }
    }
}

// ==========================================
// Password Reset Flow (Via Email Link)
// ==========================================

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