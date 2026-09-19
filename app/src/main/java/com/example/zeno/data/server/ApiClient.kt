package com.example.zeno.data.server

import android.content.Context
import com.example.zeno.core.data.EncryptedAuthStorageImpl
import com.example.zeno.data.local.TokenManager
import com.example.zeno.data.model.server.RefreshRequest
import com.example.zeno.data.serverConnections.*
import com.example.zeno.features.home.data.ProgressApi
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.runBlocking

private const val BASE_URL = "https://zenohostingserver.fastapicloud.dev"

class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        var accessToken = tokenManager.getAccessToken()?.trim()
        if (accessToken.isNullOrBlank()) {
            val encryptedStorage = EncryptedAuthStorageImpl(context)
            accessToken = encryptedStorage.getToken()?.trim()
        }

        if (!accessToken.isNullOrBlank()) {
            val cleanToken = if (accessToken.startsWith("Bearer ", ignoreCase = true)) {
                accessToken
            } else {
                "Bearer $accessToken"
            }
            requestBuilder.header("Authorization", cleanToken)
        }

        return chain.proceed(requestBuilder.build())
    }
}

class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val context: Context
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            val path = response.request.url.encodedPath
            if (path.contains("/auth/login") || path.contains("/auth/register") || path.contains("/auth/refresh")) {
                return null
            }

            synchronized(this) {
                var currentToken = tokenManager.getAccessToken()
                if (currentToken.isNullOrBlank()) {
                    currentToken = EncryptedAuthStorageImpl(context).getToken()
                }
                val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")?.trim()

                // If token changed while waiting, retry with new token
                if (!currentToken.isNullOrBlank() && currentToken != requestToken) {
                    return response.request.newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .build()
                }

                var refreshToken = tokenManager.getRefreshToken()
                if (refreshToken.isNullOrBlank()) {
                    refreshToken = EncryptedAuthStorageImpl(context).getRefreshToken()
                }

                if (!refreshToken.isNullOrBlank()) {
                    try {
                        val refreshRetrofit = Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                        val authApi = refreshRetrofit.create(AuthApi::class.java)
                        
                        val refreshResponse = runBlocking {
                            authApi.refresh(RefreshRequest(refreshToken))
                        }

                        tokenManager.saveTokens(
                            accessToken = refreshResponse.accessToken,
                            refreshToken = refreshResponse.refreshToken ?: ""
                        )
                        EncryptedAuthStorageImpl(context).saveTokens(
                            accessToken = refreshResponse.accessToken,
                            refreshToken = refreshResponse.refreshToken
                        )

                        return response.request.newBuilder()
                            .header("Authorization", "Bearer ${refreshResponse.accessToken}")
                            .header("ngrok-skip-browser-warning", "true")
                            .build()
                    } catch (e: Exception) {
                        tokenManager.clearTokens()
                        EncryptedAuthStorageImpl(context).clearToken()
                    }
                } else {
                    tokenManager.clearTokens()
                    EncryptedAuthStorageImpl(context).clearToken()
                }
            }
        }
        return null
    }
}

object ApiClient {
    private lateinit var tokenManager: TokenManager
    private lateinit var retrofit: Retrofit
    lateinit var context: Context

    fun initialize(context: Context) {
        this.context = context.applicationContext
        tokenManager = TokenManager(context.applicationContext)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(tokenManager, context.applicationContext))
            .authenticator(TokenAuthenticator(tokenManager, context.applicationContext))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun checkInitialized() {
        check(::retrofit.isInitialized) {
            "ApiClient.initialize(context) must be called first."
        }
    }

    fun auth(): AuthApi {
        checkInitialized()
        return retrofit.create(AuthApi::class.java)
    }

    fun chat(): ChatApi {
        checkInitialized()
        return retrofit.create(ChatApi::class.java)
    }

    fun user(): UserApi {
        checkInitialized()
        return retrofit.create(UserApi::class.java)
    }

    fun subscription(): SubscriptionApi {
        checkInitialized()
        return retrofit.create(SubscriptionApi::class.java)
    }

    fun progress(): ProgressApi {
        checkInitialized()
        return retrofit.create(ProgressApi::class.java)
    }

    fun tokens(): TokenApi {
        checkInitialized()
        return retrofit.create(TokenApi::class.java)
    }

    fun payments(): PaymentApi {
        checkInitialized()
        return retrofit.create(PaymentApi::class.java)
    }

    fun health(): HealthApi {
        checkInitialized()
        return retrofit.create(HealthApi::class.java)
    }

    fun tokenManager(): TokenManager {
        checkInitialized()
        return tokenManager
    }

    fun notification(): com.example.zeno.features.notification.data.NotificationApi {
        checkInitialized()
        return retrofit.create(com.example.zeno.features.notification.data.NotificationApi::class.java)
    }
}
