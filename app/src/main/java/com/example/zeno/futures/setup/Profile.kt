package com.example.zeno.futures.setup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.zeno.core.sections.setup.NameAge.ProfileMiddleSection
import com.example.zeno.data.local.UserManager

@Composable
fun SetupProfile(
    onContinue: () -> Unit,
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    ProfileMiddleSection(
        { name, birthDate ->
            userManager.saveProfileData(
                displayName = name,
                birthDate = birthDate
            )
            onContinue()
        }
    )
}