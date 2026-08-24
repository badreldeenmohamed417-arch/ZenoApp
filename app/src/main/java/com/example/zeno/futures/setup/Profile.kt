package com.example.zeno.futures.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.zeno.core.sections.setup.LetUsKnowYou
import com.example.zeno.core.sections.setup.NameAge.ProfileMiddleSection
import com.example.zeno.core.txt
import com.example.zeno.data.local.UserManager
import com.example.zeno.futures.MessageType
import com.example.zeno.futures.TopMessage
import kotlinx.coroutines.delay

@Composable
fun SetupProfile(
    onContinue: () -> Unit,
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // التحكم في عرض وتوقيت إخفاء التنبيه تلقائياً
    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->

            delay(3000) // يختفي الخطأ بعد 3 ثوانٍ
            errorMessage = null
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopMessage(
            message = errorMessage ?: txt(""),
            visible = errorMessage != null,
            type = MessageType.ERROR,
        )
        LetUsKnowYou()

        ProfileMiddleSection(
            continueButton = { name, birthDate ->
                userManager.saveProfileData(
                    displayName = name,
                    birthDate = birthDate
                )
                onContinue()
            },
            onError = { message ->
                errorMessage = message
            },
            modifier = Modifier.weight(1f)
        )
    }
}