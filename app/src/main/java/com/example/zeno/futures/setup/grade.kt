package com.example.zeno.futures.setup

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.zeno.core.sections.setup.Grade.GradeMiddleSection
import com.example.zeno.data.local.UserManager
import com.example.zeno.data.repository.AuthRepository
import com.example.zeno.futures.completeUserData
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun SetupGrade(
    onContinue: () -> Unit,
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val authRepository = remember { AuthRepository() }

    var error by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    GradeMiddleSection(
        { grade, schoolSystem ->
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
        }
    )
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

    completeUserData(
        authRepository = authRepository,
        displayName = displayName,
        grade = grade,
        schoolSystem = schoolSystem,
        language = language,
        onContinue = onContinue,
        errorFun = errorFun,
        disableError = disableError
    )
}