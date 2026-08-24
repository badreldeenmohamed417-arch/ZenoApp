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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.zeno.data.AppColors
import com.example.zeno.data.local.TokenManager
import com.example.zeno.data.local.UserManager
import com.example.zeno.data.server.ApiClient
import com.example.zeno.futures.Screen
import com.example.zeno.futures.ZenoNavHost
import com.example.zeno.ui.theme.ZenoTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val userManager = UserManager(this)
        val language = userManager.getLanguage()
        updateLocale(language)

        ApiClient.initialize(this)
        val tokenManager = ApiClient.tokenManager()

        val startDestination = when {
            tokenManager.getAccessToken().isNullOrBlank() -> Screen.Login.route
            userManager.getDisplayName().isNullOrBlank() -> Screen.SetupProfile.route
            else -> Screen.Main.route
        }

        enableEdgeToEdge()
        setContent {
            val isDarkTheme = userManager.getThemeMode(androidx.compose.foundation.isSystemInDarkTheme())
            ZenoTheme(darkTheme = isDarkTheme) {
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

    private fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}