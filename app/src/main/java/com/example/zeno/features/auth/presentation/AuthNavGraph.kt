package com.example.zeno.features.auth.presentation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zeno.features.auth.data.AuthRepository

import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.zeno.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun AuthNavGraph(
    authRepository: AuthRepository,
    onAuthSuccess: () -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    val handleGoogleSignIn: () -> Unit = {
        coroutineScope.launch {
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

                val googleCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                
                val apiResult = authRepository.googleLogin(googleCredential.idToken)
                
                if (apiResult.isSuccess) {
                    onAuthSuccess()
                } else {
                    Toast.makeText(context, "Login failed: ${apiResult.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("AuthNavGraph", "Google Sign-In error", e)
                Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                authRepository = authRepository,
                onLoginSuccess = onAuthSuccess,
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                onGoogleSignIn = handleGoogleSignIn
            )
        }
        
        composable("register") {
            RegisterScreen(
                authRepository = authRepository,
                onRegisterSuccess = onAuthSuccess,
                onNavigateToLogin = { navController.popBackStack() },
                onGoogleSignIn = handleGoogleSignIn
            )
        }
        
        composable("forgot_password") {
            ForgotPasswordScreen(
                authRepository = authRepository,
                onNavigateBackToLogin = { navController.popBackStack() }
            )
        }
    }
}
