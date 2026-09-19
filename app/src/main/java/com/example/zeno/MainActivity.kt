package com.example.zeno

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import org.koin.android.ext.android.inject
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.zeno.core.navigation.RootNavGraph
import com.example.zeno.core.network.AuthInterceptor
import com.example.zeno.core.network.RetrofitClient
import com.example.zeno.core.data.EncryptedAuthStorageImpl
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.zeno.core.theme.ZenoTheme
import com.example.zeno.core.network.TokenAuthenticator
import com.example.zeno.features.auth.data.AuthApi
import com.example.zeno.features.auth.data.AuthRepository

import com.example.zeno.core.billing.BillingManager
import com.example.zeno.data.local.UserManager
import com.example.zeno.data.server.ApiClient
import com.example.zeno.features.chat.data.ChatApi
import com.example.zeno.features.chat.data.repository.ChatRepository
import com.example.zeno.features.student.data.StudentApi
import com.example.zeno.features.student.data.repository.StudentRepository
import com.example.zeno.features.home.data.ProgressApi
import com.example.zeno.features.home.data.repository.ProgressRepository
import com.example.zeno.features.session.data.StudyPlanApi
import com.example.zeno.features.session.data.repository.StudyPlanRepository
import com.example.zeno.features.session.data.SessionApi
import com.example.zeno.features.session.data.repository.SessionRepository
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val billingManager: BillingManager by inject()
    override fun attachBaseContext(newBase: Context) {
        val userManager = UserManager(newBase)
        val lang = userManager.getLanguage()
        val locale = Locale.forLanguageTag(lang)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userManager = com.example.zeno.data.local.UserManager(this)

        val pushNotificationService = com.example.zeno.features.notification.data.ZenoFirebaseMessagingServiceImpl()
        pushNotificationService.initialize()
        
        installSplashScreen()
        
        enableEdgeToEdge()
        setContent {
            val language = userManager.getLanguage()
            val layoutDirection = if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
            
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                ZenoTheme {
                    Surface {
                        RootNavGraph()
                    }
                }
            }
        }
    }
}