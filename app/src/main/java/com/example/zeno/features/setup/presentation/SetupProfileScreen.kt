package com.example.zeno.features.setup.presentation

import org.koin.compose.koinInject

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.zeno.R
import com.example.zeno.core.sections.setup.Grade.GradeMiddleSection
import com.example.zeno.data.AppColors
import com.example.zeno.data.local.UserManager

import com.example.zeno.features.auth.data.AuthRepository
import com.example.zeno.features.auth.data.CompleteDataRequest
import kotlinx.coroutines.launch

@Composable
fun SetupProfileScreen(
    authRepository: AuthRepository,
    onSetupComplete: () -> Unit
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var usernameInput by remember { mutableStateOf(userManager.getDisplayName() ?: "") }
    val requiresUsername = userManager.getRequiresUsername()

    val requiredUsernameError = stringResource(R.string.setup_username_required_error)
    val usernameTakenError = stringResource(R.string.setup_username_taken_error)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BG)
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        Image(
            painter = painterResource(id = R.drawable.ic_zeno_logo),
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            colorFilter = ColorFilter.tint(AppColors.Accent)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = stringResource(id = R.string.setup_title),
            style = MaterialTheme.typography.headlineMedium,
            color = AppColors.TextPrimary
        )
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Text(
            text = stringResource(id = R.string.setup_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = AppColors.TextMuted,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        if (requiresUsername) {
            // Compulsory Username Input
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.setup_username_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = usernameInput,
                    onValueChange = {
                        usernameInput = it
                        errorMessage = null
                    },
                    placeholder = { Text(stringResource(R.string.setup_username_hint), color = AppColors.TextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Accent,
                        unfocusedBorderColor = AppColors.UnfocusedBorder,
                        focusedContainerColor = AppColors.Surface,
                        unfocusedContainerColor = AppColors.Surface,
                        focusedTextColor = AppColors.TextPrimary,
                        unfocusedTextColor = AppColors.TextPrimary
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        GradeMiddleSection(
            continueButton = { selectedGrade, selectedSystem, selectedTrack ->
                if (requiresUsername) {
                    if (usernameInput.trim().isBlank()) {
                        errorMessage = requiredUsernameError
                        return@GradeMiddleSection
                    }
                    if (usernameInput.trim().length < 2 || usernameInput.trim().length > 15) {
                        errorMessage = "اسم المستخدم يجب أن يكون بين 2 و 15 حرفاً"
                        return@GradeMiddleSection
                    }
                }

                isLoading = true
                errorMessage = null

                val cleanUsername = usernameInput.trim()
                val updateData = mutableMapOf<String, String?>(
                    "grade" to selectedGrade,
                    "school_system" to selectedSystem,
                    "country" to (userManager.getCountry() ?: "EG")
                )
                
                if (requiresUsername) {
                    updateData["username"] = cleanUsername
                }

                coroutineScope.launch {
                    val userRepo = org.koin.core.context.GlobalContext.get().get<com.example.zeno.features.student.data.repository.StudentRepository>()
                    val updateMeRes = runCatching {
                        userRepo.updateProfile(updateData).getOrThrow()
                    }

                    if (updateMeRes.isFailure) {
                        val exc = updateMeRes.exceptionOrNull()
                        val msg = exc?.message ?: ""
                        if (msg.contains("taken", ignoreCase = true) || msg.contains("400") || msg.contains("مستخدم", ignoreCase = true)) {
                            isLoading = false
                            errorMessage = usernameTakenError
                            return@launch
                        }
                    }

                    userManager.saveDisplayName(cleanUsername)
                    userManager.saveAcademicData(
                        grade = selectedGrade,
                        schoolSystem = selectedSystem,
                        track = selectedTrack
                    )

                    val result = authRepository.completeData(
                        CompleteDataRequest(
                            country = userManager.getCountry() ?: "EG",
                            grade = selectedGrade,
                            schoolSystem = selectedSystem,
                            track = selectedTrack.takeIf { it.isNotBlank() },
                            displayName = cleanUsername
                        )
                    )
                    isLoading = false
                    if (result.isSuccess) {
                        userManager.saveOnboardingStatus(true)
                        onSetupComplete()
                    } else {
                        userManager.saveOnboardingStatus(true)
                        onSetupComplete()
                    }
                }
            },
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
