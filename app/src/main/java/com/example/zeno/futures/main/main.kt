package com.example.zeno.futures.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.zeno.core.sections.main.HomeScreenContent
import com.example.zeno.core.sections.main.TopSectionMainScreen
import com.example.zeno.core.widgets.BottomNavItem
import com.example.zeno.core.widgets.ZenoBottomNavigationBar

import com.example.zeno.core.txt
import androidx.compose.ui.platform.LocalContext
import com.example.zeno.data.local.UserManager

@Composable
fun MainScreen(
    onNavigationToChats: () -> Unit,
    onNavigationToSettings: () -> Unit,
    onStartSession: () -> Unit,
    onAskZeno: () -> Unit
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val guestUser = txt("guestUser")
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopSectionMainScreen(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            HomeScreenContent(
                userName = userManager.getDisplayName() ?: guestUser,
                onStartSessionClick = onStartSession,
                onAskZenoClick = onAskZeno
            )
        }
        Column(
           modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
        ) {
            ZenoBottomNavigationBar(
                selectedTab = BottomNavItem.HOME,
                onTabSelected = { item ->
                    when (item) {
                        BottomNavItem.CHATS -> {
                            onNavigationToChats()
                        }
                        BottomNavItem.SETTINGS -> {
                            onNavigationToSettings()
                        }
                        else -> {}
                    }
                }
            )
        }
    }
}