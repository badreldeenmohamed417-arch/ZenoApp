package com.example.zeno.core.config.data.repository

import com.example.zeno.core.config.data.AppConfigResponse
import com.example.zeno.core.config.data.ConfigApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class ConfigRepository(private val api: ConfigApi) {
    
    private val _configState = MutableStateFlow<AppConfigResponse?>(null)
    val configState: StateFlow<AppConfigResponse?> = _configState.asStateFlow()
    
    suspend fun fetchConfig() {
        withContext(Dispatchers.IO) {
            try {
                val response = api.getAppConfig()
                _configState.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun getConfig(): AppConfigResponse? = _configState.value
}
