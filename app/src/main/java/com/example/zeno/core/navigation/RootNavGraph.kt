package com.example.zeno.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zeno.features.auth.data.AuthRepository
import com.example.zeno.features.auth.presentation.AuthNavGraph
import com.example.zeno.features.main.presentation.MainAppScreen
import com.example.zeno.features.setup.presentation.SetupProfileScreen

import com.example.zeno.features.student.data.repository.StudentRepository
import com.example.zeno.features.chat.data.repository.ChatRepository
import com.example.zeno.features.session.data.repository.SessionRepository
import com.example.zeno.features.social.data.repository.FriendsRepository

@Composable
fun RootNavGraph(
    authRepository: AuthRepository, 
    studentRepository: StudentRepository,
    chatRepository: ChatRepository,
    sessionRepository: SessionRepository,
    friendsRepository: FriendsRepository
) {
    val navController = rememberNavController()

    // Splash/Auth Logic Router
    val startDestination = if (authRepository.isLoggedIn()) {
        "main" // In a real app, we check if setup is complete. Assuming setup is done for now or redirect to setup
    } else {
        "auth"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        
        composable("auth") {
            AuthNavGraph(
                authRepository = authRepository,
                onAuthSuccess = {
                    navController.navigate("setup") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        
        composable("setup") {
            SetupProfileScreen(
                authRepository = authRepository,
                onSetupComplete = {
                    navController.navigate("main") {
                        popUpTo("setup") { inclusive = true }
                    }
                }
            )
        }
        
        composable("main") {
            MainAppScreen(
                studentRepository = studentRepository, 
                chatRepository = chatRepository,
                sessionRepository = sessionRepository,
                friendsRepository = friendsRepository
            )
        }
    }
}
