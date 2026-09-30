package com.example.zeno.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zeno.data.local.UserManager
import com.example.zeno.features.assessment.presentation.AssessmentChatScreen
import com.example.zeno.features.auth.data.AuthRepository
import com.example.zeno.features.auth.presentation.AuthNavGraph
import com.example.zeno.features.auth.presentation.EmailVerificationScreen
import com.example.zeno.features.auth.presentation.EmailVerificationViewModel
import com.example.zeno.features.auth.presentation.LanguageSelectionScreen
import com.example.zeno.features.home.data.repository.ProgressRepository
import com.example.zeno.features.main.presentation.MainAppScreen
import com.example.zeno.features.session.data.repository.SessionRepository
import com.example.zeno.features.session.data.repository.StudyPlanRepository
import com.example.zeno.features.setup.presentation.SetupProfileScreen
import com.example.zeno.features.student.data.repository.StudentRepository
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun RootNavGraph() {
    // Only resolve the authentication dependency at the root.
    // Other repositories are resolved only when their destination is opened.
    val authRepository: AuthRepository = koinInject()
    val isLoggedIn = authRepository.isLoggedIn()

    val studentRepository: StudentRepository? =
        if (isLoggedIn) koinInject() else null

    val navController = rememberNavController()
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    val startDestination = when {
        !userManager.isInitialLanguageSelected() -> "language_selection"
        !isLoggedIn -> "auth"
        !userManager.isOnboarded() -> "setup"
        !userManager.isAssessmentCompleted() -> "assessment"
        else -> "main"
    }

    LaunchedEffect(isLoggedIn) {
        val repository = studentRepository ?: return@LaunchedEffect
        try {
            val result = repository.getProfile()
            if (result.isSuccess) {
                val profile = result.getOrNull()
                val isUsernameMissing = profile?.username.isNullOrBlank()
                val isGradeMissing = profile?.grade.isNullOrBlank()
                val isSystemMissing = profile?.schoolSystem.isNullOrBlank()
                val isServerOnboarded = profile?.isOnboarded == true
                val isSetupIncomplete =
                    !isServerOnboarded || isUsernameMissing || isGradeMissing || isSystemMissing

                userManager.saveOnboardingStatus(!isSetupIncomplete)
                userManager.saveRequiresUsername(isUsernameMissing)

                val currentRoute = navController.currentDestination?.route
                if (profile?.isVerified == false && profile.authProvider != "google") {
                    if (currentRoute != "email_verification") {
                        navController.navigate("email_verification") { popUpTo(0) }
                    }
                } else if (isSetupIncomplete && currentRoute == "main") {
                    navController.navigate("setup") { popUpTo(0) }
                }
            } else {
                val exception = result.exceptionOrNull()
                if (exception is retrofit2.HttpException && exception.code() == 401) {
                    authRepository.logout()
                    userManager.clearUserData()
                    navController.navigate("auth") { popUpTo(0) }
                }
            }
        } catch (_: Exception) {
            // Network failures must never prevent the app from opening.
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("language_selection") {
            LanguageSelectionScreen(
                onContinue = {
                    val nextDest = when {
                        !authRepository.isLoggedIn() -> "auth"
                        !userManager.isOnboarded() -> "setup"
                        !userManager.isAssessmentCompleted() -> "assessment"
                        else -> "main"
                    }
                    navController.navigate(nextDest) {
                        popUpTo("language_selection") { inclusive = true }
                    }
                }
            )
        }

        composable("auth") {
            AuthNavGraph(
                authRepository = authRepository,
                onAuthSuccess = {
                    val nextDest = when {
                        !userManager.isOnboarded() -> "setup"
                        !userManager.isAssessmentCompleted() -> "assessment"
                        else -> "main"
                    }
                    navController.navigate(nextDest) {
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
                    val nextDest = when {
                        !userManager.isOnboarded() -> "setup"
                        !userManager.isAssessmentCompleted() -> "assessment"
                        else -> "main"
                    }
                    navController.navigate(nextDest) {
                        popUpTo("email_verification") { inclusive = true }
                    }
                }
            }

            EmailVerificationScreen(
                onVerified = {
                    val nextDest = when {
                        !userManager.isOnboarded() -> "setup"
                        !userManager.isAssessmentCompleted() -> "assessment"
                        else -> "main"
                    }
                    navController.navigate(nextDest) {
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
            val studyPlanRepository: StudyPlanRepository = koinInject()
            val configRepository: com.example.zeno.core.config.data.repository.ConfigRepository = koinInject()
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
            val studentRepositoryForMain: StudentRepository = koinInject()
            val progressRepository: ProgressRepository = koinInject()
            val chatRepository: com.example.zeno.features.chat.data.repository.ChatRepository = koinInject()
            val sessionRepository: SessionRepository = koinInject()
            val studyPlanRepository: StudyPlanRepository = koinInject()
            val configRepository: com.example.zeno.core.config.data.repository.ConfigRepository = koinInject()

            MainAppScreen(
                studentRepository = studentRepositoryForMain,
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
