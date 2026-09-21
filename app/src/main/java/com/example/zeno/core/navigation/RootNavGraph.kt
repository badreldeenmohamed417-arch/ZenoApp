package com.example.zeno.core.navigation
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import org.koin.compose.koinInject

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zeno.core.SplashScreenContent
import com.example.zeno.data.local.UserManager
import com.example.zeno.features.assessment.presentation.AssessmentChatScreen
import com.example.zeno.features.auth.data.AuthRepository
import com.example.zeno.features.auth.presentation.AuthNavGraph
import com.example.zeno.features.auth.presentation.LanguageSelectionScreen
import com.example.zeno.features.chat.data.repository.ChatRepository
import com.example.zeno.features.home.data.repository.ProgressRepository
import com.example.zeno.features.main.presentation.MainAppScreen
import com.example.zeno.features.session.data.repository.SessionRepository
import com.example.zeno.features.session.data.repository.StudyPlanRepository
import com.example.zeno.features.setup.presentation.SetupProfileScreen
import com.example.zeno.features.auth.presentation.EmailVerificationScreen
import com.example.zeno.features.auth.presentation.EmailVerificationViewModel
import org.koin.androidx.compose.koinViewModel
import com.example.zeno.features.student.data.repository.StudentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.HttpException

@Composable
fun RootNavGraph(

) {
        val authRepository: AuthRepository = koinInject()
    val studentRepository: StudentRepository = koinInject()
    val progressRepository: ProgressRepository = koinInject()
    val chatRepository: ChatRepository = koinInject()
    val sessionRepository: SessionRepository = koinInject()
    val studyPlanRepository: StudyPlanRepository = koinInject()
    val configRepository = org.koin.core.context.GlobalContext.get().get<com.example.zeno.core.config.data.repository.ConfigRepository>()
    val navController = rememberNavController()
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    val startDestination = if (!userManager.isInitialLanguageSelected()) {
        "language_selection"
    } else if (authRepository.isLoggedIn()) {
        "splash"
    } else {
        "auth"
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable("language_selection") {
            LanguageSelectionScreen(
                onContinue = {
                    val nextDest = if (authRepository.isLoggedIn()) "splash" else "auth"
                    navController.navigate(nextDest) {
                        popUpTo("language_selection") { inclusive = true }
                    }
                }
            )
        }

        composable("splash") {
            LaunchedEffect(Unit) {

                if (!authRepository.isLoggedIn()) {
                    navController.navigate("auth") {
                        popUpTo("splash") { inclusive = true }
                    }
                    return@LaunchedEffect
                }

                try {
                    val result = withTimeoutOrNull(2500) {
                        studentRepository.getProfile()
                    }
                    
                    if (result != null && result.isSuccess) {
                        val profile = result.getOrNull()
                        val isUsernameMissing = profile?.username.isNullOrBlank()
                        val isGradeMissing = profile?.grade.isNullOrBlank()
                        val isSystemMissing = profile?.schoolSystem.isNullOrBlank()
                        val isServerOnboarded = profile?.isOnboarded == true

                        val isSetupIncomplete = !isServerOnboarded || isUsernameMissing || isGradeMissing || isSystemMissing

                        if (isSetupIncomplete) {
                            userManager.saveOnboardingStatus(false)
                        } else {
                            userManager.saveOnboardingStatus(true)
                        }
                        
                        // Save whether the user needs to provide a username
                        userManager.saveRequiresUsername(isUsernameMissing)

                        val nextDest = if (profile?.isVerified == false && profile.authProvider != "google") {
                            "email_verification"
                        } else if (isSetupIncomplete || !userManager.isOnboarded()) {
                            "setup"
                        } else if (!userManager.isAssessmentCompleted()) {
                            "assessment"
                        } else {
                            "main"
                        }
                        navController.navigate(nextDest) {
                            popUpTo("splash") { inclusive = true }
                        }
                    } else {
                        val exception = result?.exceptionOrNull()
                        if (exception is HttpException && exception.code() == 401) {
                            authRepository.logout()
                            userManager.clearUserData()
                            navController.navigate("auth") {
                                popUpTo("splash") { inclusive = true }
                            }
                        } else {
                            // No internet or timeout
                            if (userManager.isOnboarded() && userManager.isAssessmentCompleted()) {
                                navController.navigate("main") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            } else {
                                authRepository.logout()
                                navController.navigate("auth") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    if (e is HttpException && e.code() == 401) {
                        authRepository.logout()
                        navController.navigate("auth") {
                            popUpTo("splash") { inclusive = true }
                        }
                    } else {
                        // No internet or timeout
                        if (userManager.isOnboarded() && userManager.isAssessmentCompleted()) {
                            navController.navigate("main") {
                                popUpTo("splash") { inclusive = true }
                            }
                        } else {
                            authRepository.logout()
                            navController.navigate("auth") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
        
        composable("auth") {
            AuthNavGraph(
                authRepository = authRepository,
                onAuthSuccess = {
                    navController.navigate("splash") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        
        composable("email_verification") {
            val viewModel: EmailVerificationViewModel = koinViewModel()
            val isChecking by viewModel.isChecking.collectAsState()
            val resendCooldownTimer by viewModel.resendCooldownTimer.collectAsState()
            val errorMessage by viewModel.errorMessage.collectAsState()
            val isVerified by viewModel.isVerified.collectAsState()

            LaunchedEffect(isVerified) {
                if (isVerified) {
                    navController.navigate("splash") {
                        popUpTo("email_verification") { inclusive = true }
                    }
                }
            }

            EmailVerificationScreen(
                onVerified = {
                    navController.navigate("splash") {
                        popUpTo("email_verification") { inclusive = true }
                    }
                },
                onResendEmail = { viewModel.resendEmail() },
                onCheckStatus = { viewModel.checkVerificationStatus() },
                isChecking = isChecking,
                resendCooldownTimer = resendCooldownTimer,
                errorMessage = errorMessage
            )
        }

        composable("setup") {
            SetupProfileScreen(
                authRepository = authRepository,
                onSetupComplete = {
                    navController.navigate("assessment") {
                        popUpTo("setup") { inclusive = true }
                    }
                }
            )
        }

        composable("assessment") {
            AssessmentChatScreen(
                studyPlanRepository = studyPlanRepository,
                configRepository = configRepository,
                onAssessmentComplete = {
                    navController.navigate("main") {
                        popUpTo("assessment") { inclusive = true }
                    }
                }
            )
        }
        
        composable("main") {
            MainAppScreen(
                studentRepository = studentRepository, 
                progressRepository = progressRepository,
                chatRepository = chatRepository,
                sessionRepository = sessionRepository,
                studyPlanRepository = studyPlanRepository,
                configRepository = configRepository,
                onLogout = {
                    authRepository.logout()
                    navController.navigate("auth") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}
