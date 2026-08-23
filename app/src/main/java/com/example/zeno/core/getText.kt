package com.example.zeno.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

@Composable
fun txt(key: String, vararg formatArgs: Any): String {
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(key, "string", context.packageName)
    return if (resId != 0) stringResource(id = resId, *formatArgs) else key
}