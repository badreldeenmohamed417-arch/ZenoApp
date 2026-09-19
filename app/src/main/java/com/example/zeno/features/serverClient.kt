package com.example.zeno.features

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.zeno.BuildConfig
import com.example.zeno.core.NetworkUtils
import com.example.zeno.data.local.UserManager
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
    context: Context,
    authRepository: com.example.zeno.features.auth.data.AuthRepository,
    email: String,
    password: String,
    onContinue: () -> Unit,
    errorFun: (String) -> Unit,
    disableError: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // 1. Register
            authRepository.register(
                com.example.zeno.features.auth.data.RegisterRequest(email = email, password = password)
            )

            // 2. Login immediately to get tokens for the next steps (Setup Profile)
            authRepository.login(
                com.example.zeno.features.auth.data.LoginRequest(email = email, password = password)
            )

            withContext(Dispatchers.Main) {
                disableError()
                onContinue()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                errorFun(NetworkUtils.getErrorMessage(e, context))
            }
        }
    }
}

fun login(
    context: Context,
    authRepository: com.example.zeno.features.auth.data.AuthRepository,
    email: String,
    password: String,
    onContinue: () -> Unit,
    errorFun: (String) -> Unit,
    disableError: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            authRepository.login(
                com.example.zeno.features.auth.data.LoginRequest(email = email, password = password)
            )

            withContext(Dispatchers.Main) {
                disableError()
                onContinue()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                errorFun(NetworkUtils.getErrorMessage(e, context))
            }
        }
    }
}

fun googleLogin(
    context: Context,
    authRepository: com.example.zeno.features.auth.data.AuthRepository,
    onContinue: (isNewUser: Boolean) -> Unit,
    errorFun: (String) -> Unit,
    disableError: () -> Unit
) {
    Log.d("GoogleLogin", "googleLogin started")
    CoroutineScope(Dispatchers.Main).launch {
        try {
            Log.d("GoogleLogin", "Creating CredentialManager")
            val credentialManager = CredentialManager.create(context)

            Log.d("GoogleLogin", "Building GetGoogleIdOption with Client ID: ${BuildConfig.GOOGLE_CLIENT_ID}")
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            Log.d("GoogleLogin", "Calling getCredential")
            val result = credentialManager.getCredential(
                context = context,
                request = request
            )
            Log.d("GoogleLogin", "getCredential success")

            val credential = result.credential
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val googleIdToken = googleCredential.idToken

            // Send Google ID Token to Zeno Server
            val response = withContext(Dispatchers.IO) {
                authRepository.googleLogin(googleIdToken)
            }

            // Zeno login successful
            disableError()
            onContinue(response.getOrNull()?.isNewUser == true)

        } catch (e: Exception) {
            Log.e("GoogleLogin", "Error during Google login", e)
            errorFun(context.getString(com.example.zeno.R.string.googleLoginError, e.message ?: ""))
        }
    }
}

fun completeUserData(
    context: Context,
    authRepository: com.example.zeno.features.auth.data.AuthRepository,
    country: String? = null,
    displayName: String? = null,
    grade: String? = null,
    schoolSystem: String? = null,
    track: String? = null,
    language: String = "ar",
    onContinue: () -> Unit,
    errorFun: (String) -> Unit,
    disableError: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            authRepository.completeData(
                com.example.zeno.features.auth.data.CompleteDataRequest(
                    country = country ?: "EG",
                    displayName = displayName,
                    grade = grade ?: "",
                    schoolSystem = schoolSystem,
                    track = track
                )
            )

            withContext(Dispatchers.Main) {
                disableError()
                onContinue()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                errorFun(NetworkUtils.getErrorMessage(e, context))
            }
        }
    }
}

// ==========================================
// Email Verification Flow
// ==========================================

fun checkVerificationStatus(
    context: Context,
    userRepository: com.example.zeno.features.student.data.repository.StudentRepository,
    userManager: UserManager,
    onVerified: () -> Unit,
    onNotVerified: () -> Unit,
    errorFun: (String) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val result = userRepository.getProfile()
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                userManager.saveVerificationStatus(user.isVerified)
                user.subjects?.let { userManager.saveSubjects(it) }
                
                withContext(Dispatchers.Main) {
                    if (user.isVerified) {
                        onVerified()
                    } else {
                        onNotVerified()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    errorFun(NetworkUtils.getErrorMessage(result.exceptionOrNull() ?: Exception("Unknown"), context))
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                errorFun(NetworkUtils.getErrorMessage(e, context))
            }
        }
    }
}

fun verifyEmail(
    context: Context,
    authRepository: com.example.zeno.features.auth.data.AuthRepository,
    token: String,
    onSuccess: (String) -> Unit,
    errorFun: (String) -> Unit,
    disableError: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = authRepository.verifyEmail(com.example.zeno.features.auth.data.VerifyEmailRequest(token = token))
            val result = response.getOrNull()
            
            withContext(Dispatchers.Main) {
                if (response.isSuccess && result != null) {
                    disableError()
                    onSuccess(result.message)
                } else {
                    errorFun(NetworkUtils.getErrorMessage(response.exceptionOrNull() ?: Exception("Unknown"), context))
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                errorFun(NetworkUtils.getErrorMessage(e, context))
            }
        }
    }
}

fun resendVerification(
    context: Context,
    authRepository: com.example.zeno.features.auth.data.AuthRepository,
    email: String,
    language: String = "ar",
    onSuccess: (String) -> Unit,
    errorFun: (String) -> Unit,
    disableError: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = authRepository.resendVerification(
                com.example.zeno.features.auth.data.ResendVerificationRequest(email = email)
            )
            val result = response.getOrNull()

            withContext(Dispatchers.Main) {
                if (response.isSuccess && result != null) {
                    disableError()
                    onSuccess(result.message)
                } else {
                    errorFun(NetworkUtils.getErrorMessage(response.exceptionOrNull() ?: Exception("Unknown"), context))
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                errorFun(NetworkUtils.getErrorMessage(e, context))
            }
        }
    }
}

// ==========================================
// Password Reset Flow (Via Email Link)
// ==========================================

fun forgotPassword(
    context: Context,
    authRepository: com.example.zeno.features.auth.data.AuthRepository,
    email: String,
    onSuccess: () -> Unit,
    errorFun: (String) -> Unit,
    disableError: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = authRepository.forgotPassword(com.example.zeno.features.auth.data.ForgotPasswordRequest(email = email))
            val result = response.getOrNull()

            withContext(Dispatchers.Main) {
                if (response.isSuccess) {
                    disableError()
                    onSuccess()
                } else {
                    errorFun(NetworkUtils.getErrorMessage(response.exceptionOrNull() ?: Exception("Unknown"), context))
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                errorFun(NetworkUtils.getErrorMessage(e, context))
            }
        }
    }
}