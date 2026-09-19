package com.example.zeno.core.util

import com.example.zeno.R
import com.example.zeno.data.server.ApiClient

fun Throwable?.getUserFriendlyMessage(): String {
    val context = ApiClient.context
    if (this == null) return context.getString(R.string.error_generic)
    val msg = this.message ?: ""
    return when {
        msg.contains("401") -> context.getString(R.string.error_session_expired)
        msg.contains("402") -> context.getString(R.string.error_payment_required)
        msg.contains("404") -> context.getString(R.string.error_not_found)
        msg.contains("500") || msg.contains("503") -> context.getString(R.string.error_server)
        msg.contains("Timeout") || msg.contains("ConnectException") || msg.contains("UnknownHostException") -> context.getString(R.string.error_network)
        else -> context.getString(R.string.error_generic)
    }
}
