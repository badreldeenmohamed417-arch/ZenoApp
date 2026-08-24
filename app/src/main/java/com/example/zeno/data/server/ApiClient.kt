package com.example.zeno.data.server

import android.content.Context
import com.example.zeno.data.local.TokenManager
import com.example.zeno.data.model.server.RefreshRequest
import com.example.zeno.data.serverConnections.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.runBlocking

private const val BASE_URL = "https://nexorai.top"

class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = tokenManager.getAccessToken()
        val request = chain.request().newBuilder().apply {
            if (!accessToken.isNullOrBlank()) {
                addHeader("Authorization", "Bearer $accessToken")
            }
        }.build()
        return chain.proceed(request)
    }
}

class TokenAuthenticator(
    private val tokenManager: TokenManager
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            synchronized(this) {
                val currentToken = tokenManager.getAccessToken()
                val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

                // If token changed, another request already refreshed it
                if (currentToken != null && currentToken != requestToken) {
                    return response.request.newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .build()
                }

                val refreshToken = tokenManager.getRefreshToken()
                if (refreshToken != null) {
                    try {
                        // Separate retrofit for refresh to avoid cycles
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
                            refreshToken = refreshResponse.refreshToken
                        )

                        return response.request.newBuilder()
                            .header("Authorization", "Bearer ${refreshResponse.accessToken}")
                            .build()
                    } catch (e: Exception) {
                        tokenManager.clearTokens()
                    }
                }
            }
        }
        return null
    }
}

object ApiClient {
    private lateinit var tokenManager: TokenManager
    private lateinit var retrofit: Retrofit

    fun initialize(context: Context) {
        tokenManager = TokenManager(context.applicationContext)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(tokenManager))
            .authenticator(TokenAuthenticator(tokenManager))
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
}
