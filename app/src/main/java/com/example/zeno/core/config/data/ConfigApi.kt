package com.example.zeno.core.config.data

import retrofit2.http.GET

interface ConfigApi {
    @GET("main/config")
    suspend fun getAppConfig(): AppConfigResponse
}
