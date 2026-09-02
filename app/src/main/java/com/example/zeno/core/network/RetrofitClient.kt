package com.example.zeno.core.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val MAIN_SERVER_BASE_URL = "https://zenohostingserver.fastapicloud.dev/"
    private const val AI_SERVER_BASE_URL = "https://zenohostingserver.fastapicloud.dev/"

    fun createMainServerRetrofit(authInterceptor: AuthInterceptor): Retrofit {
        return createRetrofit(MAIN_SERVER_BASE_URL, authInterceptor)
    }

    fun createAiServerRetrofit(authInterceptor: AuthInterceptor): Retrofit {
        return createRetrofit(AI_SERVER_BASE_URL, authInterceptor)
    }

    private fun createRetrofit(baseUrl: String, authInterceptor: AuthInterceptor): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
