package com.example.zeno

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.zeno.data.AppColors
import com.example.zeno.data.local.TokenManager
import com.example.zeno.data.local.UserManager
import com.example.zeno.data.server.ApiClient
import com.example.zeno.futures.Screen
import com.example.zeno.futures.ZenoNavHost
import com.example.zeno.ui.theme.ZenoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        ApiClient.initialize(this)
        val tokenManager = ApiClient.tokenManager()
        val userManager = UserManager(this)

        val startDestination = when {
            tokenManager.getAccessToken().isNullOrBlank() -> Screen.Login.route
            userManager.getDisplayName().isNullOrBlank() -> Screen.SetupProfile.route
            else -> Screen.Main.route
        }

        enableEdgeToEdge()
        setContent {
            ZenoTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).background(AppColors.BG)) {
                        ZenoNavHost(
                            navController = navController,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }
}