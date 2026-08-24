package com.example.zeno.futures

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.zeno.core.SplashScreenContent
import com.example.zeno.futures.auth.ForgotPasswordScreen
import com.example.zeno.futures.auth.LoginScreen
import com.example.zeno.futures.auth.SignupScreen
import com.example.zeno.futures.main.ChatScreen
import com.example.zeno.futures.main.ConversationsScreen
import com.example.zeno.futures.main.MainScreen
import com.example.zeno.futures.main.SettingsScreen
import com.example.zeno.futures.main.TermsOfUseScreen
import com.example.zeno.futures.premium.PremiumScreen
import com.example.zeno.futures.session.StudySessionScreen
import com.example.zeno.futures.setup.SetupGrade
import com.example.zeno.futures.setup.SetupProfile
import kotlinx.coroutines.launch

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
    object TermsOfUse : Screen("terms_of_use")
    object Premium : Screen("premium")
    object Conversations : Screen("conversations")
    object Chat : Screen("chat/{conversationId}") {
        fun createRoute(conversationId: String?) = if (conversationId == null) "chat/new" else "chat/$conversationId"
    }
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
                onAskZeno = { navController.navigate(Screen.Chat.createRoute(null)) }
            )
        }

        composable(Screen.Conversations.route) {
            ConversationsScreen(
                onBack = { navController.popBackStack() },
                onHome = { navController.navigate(Screen.Main.route) { popUpTo(Screen.Main.route) { inclusive = true } } },
                onSettings = { navController.navigate(Screen.Settings.route) },
                onNewChat = { navController.navigate(Screen.Chat.createRoute(null)) },
                onConversationClick = { id -> navController.navigate(Screen.Chat.createRoute(id)) }
            )
        }

        composable(Screen.Chat.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("conversationId")
            ChatScreen(
                conversationId = if (id == "new") null else id,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val scope = rememberCoroutineScope()
            SettingsScreen(
                onLogout = { 
                    scope.launch {
                        com.example.zeno.data.local.db.AppDatabase.getDatabase(context).chatDao().clearConversations()
                        com.example.zeno.data.local.db.AppDatabase.getDatabase(context).chatDao().clearMessages()
                        com.example.zeno.data.local.UserManager(context).clearUserData()
                        com.example.zeno.data.server.ApiClient.tokenManager().clearTokens()
                        navController.navigate(Screen.Login.route) { 
                            popUpTo(0)
                        }
                    }
                },
                onHome = { navController.navigate(Screen.Main.route) { popUpTo(Screen.Main.route) { inclusive = true } } },
                onChats = { navController.navigate(Screen.Conversations.route) },
                onTermsOfUse = { navController.navigate(Screen.TermsOfUse.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TermsOfUse.route) {
            TermsOfUseScreen(
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
