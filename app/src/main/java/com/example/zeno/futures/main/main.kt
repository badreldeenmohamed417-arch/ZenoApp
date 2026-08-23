package com.example.zeno.futures.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.zeno.core.sections.main.HomeScreenContent
import com.example.zeno.core.sections.main.TopSectionMainScreen
import com.example.zeno.core.widgets.BottomNavItem
import com.example.zeno.core.widgets.ZenoBottomNavigationBar

@Composable
fun MainScreen(
    onNavigationToChats: () -> Unit,
    onNavigationToSettings: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopSectionMainScreen(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            HomeScreenContent()
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