package com.example.zeno.futures

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.zeno.core.SplashScreenContent
import com.example.zeno.futures.auth.ForgotPasswordScreen
import com.example.zeno.futures.auth.LoginScreen
import com.example.zeno.futures.auth.SignupScreen
import com.example.zeno.futures.main.ConversationsScreen
import com.example.zeno.futures.main.MainScreen
import com.example.zeno.futures.main.SettingsScreen
import com.example.zeno.futures.premium.PremiumScreen
import com.example.zeno.futures.session.StudySessionScreen
import com.example.zeno.futures.setup.SetupGrade
import com.example.zeno.futures.setup.SetupProfile

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object ForgotPassword : Screen("forgot_password")
    object MailSent : Screen("mail_sent/{email}/{type}") {
        fun createRoute(email: String, type: String) = "mail_sent/$email/$type"
    }
    object SetupProfile : Screen("setup_profile")
    object SetupGrade : Screen("setup_grade")
    object Main : Screen("main")
    object StudySession : Screen("study_session")
    object Settings : Screen("settings")
    object Premium : Screen("premium")
    object Conversations : Screen("conversations")
}

@Composable
fun ZenoNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreenContent(
                onFinished = {
                    // This logic is currently in MainActivity startDestination
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onContinue = { navController.navigate(Screen.Main.route) { popUpTo(0) } },
                forgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                register = { navController.navigate(Screen.Signup.route) },
                setup = { navController.navigate(Screen.SetupProfile.route) }
            )
        }

        composable(Screen.Signup.route) {
            SignupScreen(
                onContinue = { navController.navigate(Screen.Main.route) { popUpTo(0) } },
                login = { navController.popBackStack() },
                setup = { navController.navigate(Screen.SetupProfile.route) }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onContinue = { /* Navigate to MailSent */ },
                login = { navController.popBackStack() }
            )
        }

        composable(Screen.SetupProfile.route) {
            SetupProfile(
                onContinue = { navController.navigate(Screen.SetupGrade.route) }
            )
        }

        composable(Screen.SetupGrade.route) {
            SetupGrade(
                onContinue = { navController.navigate(Screen.Main.route) { popUpTo(0) } }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                onNavigationToChats = { navController.navigate(Screen.Conversations.route) },
                onNavigationToSettings = { navController.navigate(Screen.Settings.route) },
                onStartSession = { navController.navigate(Screen.StudySession.route) },
                onAskZeno = { /* Handled via home state probably, or navigate to a specialized chat */ }
            )
        }

        composable(Screen.Conversations.route) {
            ConversationsScreen(
                onBack = { navController.popBackStack() },
                onConversationClick = { /* Navigate to specific chat */ }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onLogout = { 
                    navController.navigate(Screen.Login.route) { 
                        popUpTo(0)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Premium.route) {
            PremiumScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.StudySession.route) {
            StudySessionScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
