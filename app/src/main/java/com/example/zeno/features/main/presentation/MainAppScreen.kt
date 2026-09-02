package com.example.zeno.features.main.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.zeno.R
import com.example.zeno.core.theme.ZenoTheme
import com.example.zeno.features.chat.presentation.ChatScreen
import com.example.zeno.features.chat.presentation.ChatViewModel
import com.example.zeno.features.chat.data.repository.ChatRepository
import com.example.zeno.features.home.presentation.HomeScreen
import com.example.zeno.features.home.presentation.HomeViewModel
import com.example.zeno.features.profile.presentation.SettingsMainScreen
import com.example.zeno.features.profile.presentation.ProfileViewModel
import com.example.zeno.features.session.presentation.SessionsScreen
import com.example.zeno.features.student.data.repository.StudentRepository
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zeno.features.session.presentation.SessionsViewModel
import com.example.zeno.features.session.data.repository.SessionRepository
import com.example.zeno.features.social.presentation.FriendsViewModel
import com.example.zeno.features.social.data.repository.FriendsRepository
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

sealed class BottomNavItem(val route: String, val titleResId: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : BottomNavItem("home", R.string.nav_home, Icons.Default.Home)
    object Chat : BottomNavItem("chat", R.string.nav_chat, Icons.AutoMirrored.Filled.Chat)
    object Sessions : BottomNavItem("sessions", R.string.nav_sessions, Icons.Default.MenuBook)
    object Profile : BottomNavItem("profile", R.string.nav_profile, Icons.Default.Person)
}

@Composable
fun MainAppScreen(
    studentRepository: StudentRepository, 
    chatRepository: ChatRepository,
    sessionRepository: SessionRepository,
    friendsRepository: FriendsRepository
) {
    val navController = rememberNavController()
    
    val homeViewModel: HomeViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(studentRepository) as T
    })
    
    val profileViewModel: ProfileViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ProfileViewModel(studentRepository) as T
    })

    val chatViewModel: ChatViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ChatViewModel(chatRepository) as T
    })

    val sessionsViewModel: SessionsViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = SessionsViewModel(sessionRepository) as T
    })

    val friendsViewModel: FriendsViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = FriendsViewModel(friendsRepository) as T
    })
    
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Chat,
        BottomNavItem.Sessions,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = stringResource(id = item.titleResId)) },
                        label = { Text(stringResource(id = item.titleResId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(viewModel = homeViewModel)
            }
            composable(BottomNavItem.Chat.route) {
                ChatScreen(viewModel = chatViewModel)
            }
            composable(BottomNavItem.Sessions.route) {
                SessionsScreen(viewModel = sessionsViewModel)
            }
            composable(BottomNavItem.Profile.route) {
                SettingsMainScreen(viewModel = profileViewModel)
            }
            composable("friends") {
                com.example.zeno.features.social.presentation.FriendsScreen(navController, viewModel = friendsViewModel)
            }
        }
    }
}
