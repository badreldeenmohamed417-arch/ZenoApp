package com.example.zeno.features.main.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.zeno.features.session.SessionPhase
import com.example.zeno.features.session.StudySessionService
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.zeno.R
import com.example.zeno.core.widgets.FloatingZenoBottomBar
import com.example.zeno.data.local.UserManager
import com.example.zeno.features.chat.data.repository.ChatRepository
import com.example.zeno.features.chat.presentation.ChatScreen
import com.example.zeno.features.chat.presentation.ChatViewModel
import com.example.zeno.features.home.data.repository.ProgressRepository
import com.example.zeno.features.home.presentation.HomeScreen
import com.example.zeno.features.home.presentation.HomeViewModel
import com.example.zeno.features.premium.PremiumScreen
import com.example.zeno.features.premium.presentation.PremiumViewModel
import com.example.zeno.features.profile.presentation.ProfileViewModel
import com.example.zeno.features.profile.presentation.SettingsMainScreen
import com.example.zeno.features.session.StudySessionScreen
import com.example.zeno.features.session.data.repository.SessionRepository
import com.example.zeno.features.session.data.repository.StudyPlanRepository
import com.example.zeno.features.session.presentation.SessionsScreen
import com.example.zeno.features.session.presentation.SessionsViewModel
import com.example.zeno.features.student.data.repository.StudentRepository

sealed class BottomNavItem(val route: String, val titleResId: Int, val icon: ImageVector) {
    object Home : BottomNavItem("home", R.string.nav_home, Icons.Default.Home)
    object Chat : BottomNavItem("chat", R.string.nav_chat, Icons.AutoMirrored.Filled.Chat)
    object Sessions : BottomNavItem("sessions", R.string.nav_sessions, Icons.Default.MenuBook)
    object Upgrade : BottomNavItem("upgrade", R.string.nav_upgrade, Icons.Default.WorkspacePremium)
    object Profile : BottomNavItem("profile", R.string.nav_profile, Icons.Default.Person)
}

@Composable
fun MainAppScreen(
    studentRepository: StudentRepository,
    progressRepository: ProgressRepository,
    chatRepository: ChatRepository,
    sessionRepository: SessionRepository,
    studyPlanRepository: StudyPlanRepository,
    configRepository: com.example.zeno.core.config.data.repository.ConfigRepository,
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val homeViewModel: HomeViewModel = koinViewModel()

    val profileViewModel: ProfileViewModel = koinViewModel()

    val userManager = remember { UserManager(context) }
    val chatViewModel: ChatViewModel = koinViewModel()

    val sessionsViewModel: SessionsViewModel = koinViewModel()

    
    val premiumViewModel: PremiumViewModel = koinViewModel()

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Chat,
        BottomNavItem.Sessions,
        BottomNavItem.Upgrade,
        BottomNavItem.Profile
    )

    var isSettingsSubRouteActive by remember { mutableStateOf(false) }
    val sessionState by StudySessionService.sessionState.collectAsState()
    val isSessionActive = sessionState.phase != SessionPhase.IDLE

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val isImeVisible = WindowInsets.ime.asPaddingValues().calculateBottomPadding() > 0.dp
    val showBottomBar = !isImeVisible && !isSessionActive && (currentRoute != BottomNavItem.Profile.route || !isSettingsSubRouteActive)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                FloatingZenoBottomBar(
                    items = items,
                    selectedItem = items.find { item ->
                        currentDestination?.hierarchy?.any { it.route == item.route } == true
                    } ?: items.first(),
                    onItemSelected = { item ->
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    getItemIcon = { it.icon },
                    getItemTitle = { item ->
                        if (item.titleResId != 0) {
                            stringResource(id = item.titleResId)
                        } else {
                            item.route.replaceFirstChar { char -> char.uppercase() }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->

        val navigateToTab = { route: String ->
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        val bottomPadding = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp

        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                bottom = bottomPadding
            )
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onStartSession = { navigateToTab(BottomNavItem.Sessions.route) },
                    onUpgrade = { navigateToTab(BottomNavItem.Upgrade.route) }
                )
            }
            composable(BottomNavItem.Chat.route) {
                ChatScreen(
                    viewModel = chatViewModel,
                    onNavigateToUpgrade = { navigateToTab(BottomNavItem.Upgrade.route) }
                )
            }
            composable(BottomNavItem.Sessions.route) {
                StudySessionScreen(
                    onBack = { navigateToTab(BottomNavItem.Home.route) }
                )
            }
            composable(BottomNavItem.Upgrade.route) {
                PremiumScreen(viewModel = premiumViewModel, onBack = { navController.popBackStack() })
            }
            composable(BottomNavItem.Profile.route) {
                SettingsMainScreen(
                    viewModel = profileViewModel,
                    sessionsViewModel = sessionsViewModel,
                    configRepository = configRepository,
                    onNavigateToUpgrade = { navigateToTab(BottomNavItem.Upgrade.route) },
                    onSubRouteChanged = { isSubRoute ->
                        isSettingsSubRouteActive = isSubRoute
                    }
                )
            }
        }
    }
}
