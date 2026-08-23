package com.example.zeno.data.server

import android.content.Context
import com.example.zeno.data.local.TokenManager
import com.example.zeno.data.serverConnections.AuthApi
import com.example.zeno.data.serverConnections.ChatApi
import com.example.zeno.data.serverConnections.HealthApi
import com.example.zeno.data.serverConnections.PaymentApi
import com.example.zeno.data.serverConnections.SubscriptionApi
import com.example.zeno.data.serverConnections.TokenApi
import com.example.zeno.data.serverConnections.UserApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "http://10.0.2.2:8000/"

class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val accessToken = tokenManager.getAccessToken()

        val request = chain.request()
            .newBuilder()
            .apply {
                if (!accessToken.isNullOrBlank()) {
                    addHeader(
                        "Authorization",
                        "Bearer $accessToken"
                    )
                }
            }
            .build()

        return chain.proceed(request)
    }
}

object ApiClient {

    private lateinit var tokenManager: TokenManager

    private lateinit var retrofit: Retrofit

    fun initialize(context: Context) {

        tokenManager = TokenManager(context.applicationContext)

        val client = OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor(tokenManager)
            )
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
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
