package com.example.zeno

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.zeno.core.navigation.RootNavGraph
import com.example.zeno.core.network.AuthInterceptor
import com.example.zeno.core.network.RetrofitClient
import com.example.zeno.core.data.EncryptedAuthStorageImpl
import com.example.zeno.core.theme.ZenoTheme
import com.example.zeno.features.auth.data.AuthApi
import com.example.zeno.features.auth.data.AuthRepository

import com.example.zeno.features.chat.data.ChatApi
import com.example.zeno.features.chat.data.repository.ChatRepository
import com.example.zeno.features.student.data.StudentApi
import com.example.zeno.features.student.data.repository.StudentRepository
import com.example.zeno.features.session.data.SessionApi
import com.example.zeno.features.session.data.repository.SessionRepository
import com.example.zeno.features.social.data.FriendsApi
import com.example.zeno.features.social.data.repository.FriendsRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Manual DI for Phase 9, 10, 11 & 12
        val authStorage = EncryptedAuthStorageImpl(this)
        val authInterceptor = AuthInterceptor(authStorage)
        val mainRetrofit = RetrofitClient.createMainServerRetrofit(authInterceptor)
        val aiRetrofit = RetrofitClient.createAiServerRetrofit(authInterceptor)
        
        val authApi = mainRetrofit.create(AuthApi::class.java)
        val authRepository = AuthRepository(authApi, authStorage)
        
        val studentApi = mainRetrofit.create(StudentApi::class.java)
        val studentRepository = StudentRepository(studentApi)
        
        val chatApi = aiRetrofit.create(ChatApi::class.java)
        val chatRepository = ChatRepository(chatApi)

        val sessionApi = mainRetrofit.create(SessionApi::class.java)
        val sessionRepository = SessionRepository(sessionApi)

        val friendsApi = mainRetrofit.create(FriendsApi::class.java)
        val friendsRepository = FriendsRepository(friendsApi)
        
        installSplashScreen()
        
        enableEdgeToEdge()
        setContent {
            ZenoTheme {
                Surface {
                    RootNavGraph(
                        authRepository = authRepository,
                        studentRepository = studentRepository,
                        chatRepository = chatRepository,
                        sessionRepository = sessionRepository,
                        friendsRepository = friendsRepository
                    )
                }
            }
        }
    }
}