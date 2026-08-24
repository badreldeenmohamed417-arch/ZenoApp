package com.example.zeno.core

import android.content.Context
import com.example.zeno.R
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.HttpException

object NetworkUtils {
    private val gson = Gson()

    fun getErrorMessage(e: Throwable, context: Context? = null): String {
        return when (e) {
            is HttpException -> {
                try {
                    val errorBody = e.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        val json = gson.fromJson(errorBody, JsonObject::class.java)
                        
                        // FastAPI typically returns errors in "detail"
                        if (json.has("detail")) {
                            val detail = json.get("detail")
                            if (detail.isJsonPrimitive) {
                                return detail.asString
                            } else if (detail.isJsonArray) {
                                // For validation errors, detail is a list
                                val firstError = detail.asJsonArray.get(0).asJsonObject
                                if (firstError.has("msg")) {
                                    return firstError.get("msg").asString
                                }
                            }
                        }
                    }
                    context?.getString(R.string.error_request_failed, e.code()) ?: "Request failed: ${e.code()}"
                } catch (ex: Exception) {
                    context?.getString(R.string.error_unknown) ?: "An unexpected error occurred"
                }
            }
            is java.net.UnknownHostException -> context?.getString(R.string.error_network) ?: "No internet connection"
            is java.net.SocketTimeoutException -> context?.getString(R.string.error_timeout) ?: "Connection timed out"
            else -> e.message ?: context?.getString(R.string.error_unknown) ?: "An unexpected error occurred"
        }
    }
}
