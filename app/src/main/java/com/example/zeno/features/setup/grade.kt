package com.example.zeno.futures.setup

import android.content.Context
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
import com.example.zeno.core.sections.setup.Grade.GradeMiddleSection
import com.example.zeno.core.sections.setup.LetUsKnowYou
import com.example.zeno.data.local.UserManager
import com.example.zeno.data.repository.AuthRepository
import com.example.zeno.futures.completeUserData

@Composable
fun SetupGrade(
    onContinue: () -> Unit,
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val authRepository = remember { AuthRepository() }

    var error by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        if (error && errorMessage.isNotBlank()) {
            snackbarHostState.showSnackbar(errorMessage)
            error = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LetUsKnowYou()

        GradeMiddleSection(
            continueButton = { grade, schoolSystem ->
                userManager.saveAcademicData(
                    grade = grade,
                    schoolSystem = schoolSystem
                )

                completeUserDataFromLocal(
                    context = context,
                    authRepository = authRepository,
                    onContinue = onContinue,
                    errorFun = { msg ->
                        errorMessage = msg
                        error = true
                    },
                    disableError = {
                        error = false
                    }
                )
            },
            modifier = Modifier.weight(1f)
        )
    }
}

fun completeUserDataFromLocal(
    context: Context,
    authRepository: AuthRepository,
    language: String = "ar",
    onContinue: () -> Unit,
    errorFun: (String) -> Unit,
    disableError: () -> Unit
) {
    val userManager = UserManager(context)

    val displayName = userManager.getDisplayName()
    val grade = userManager.getGrade()
    val schoolSystem = userManager.getSchoolSystem()
    val country = userManager.getCountry()

    completeUserData(
        context = context,
        authRepository = authRepository,
        country = country,
        displayName = displayName,
        grade = grade,
        schoolSystem = schoolSystem,
        language = language,
        onContinue = onContinue,
        errorFun = errorFun,
        disableError = disableError
    )
}