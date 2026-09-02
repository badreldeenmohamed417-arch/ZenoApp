package com.example.zeno.features.setup.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.zeno.R
import com.example.zeno.core.widgets.ZenoButton
import com.example.zeno.core.widgets.ZenoTextField
import com.example.zeno.features.auth.data.AuthRepository
import com.example.zeno.features.auth.data.CompleteDataRequest
import kotlinx.coroutines.launch

@Composable
fun SetupProfileScreen(
    authRepository: AuthRepository,
    onSetupComplete: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var grade by remember { mutableStateOf("") }
    var track by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        Image(
            painter = painterResource(id = R.drawable.ic_zeno_logo),
            contentDescription = "Zeno Logo",
            modifier = Modifier.size(64.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = stringResource(id = R.string.setup_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(id = R.string.setup_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // In a real app, this should be a Dropdown/ExposedDropdownMenu for selecting predefined grades.
        // For foundation purposes, we use a ZenoTextField.
        ZenoTextField(
            value = grade,
            onValueChange = { grade = it; errorMessage = null },
            placeholder = stringResource(id = R.string.setup_grade_label),
            isError = errorMessage != null
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ZenoTextField(
            value = track,
            onValueChange = { track = it; errorMessage = null },
            placeholder = stringResource(id = R.string.setup_track_label)
        )
        
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        ZenoButton(
            text = stringResource(id = R.string.setup_save_button),
            isLoading = isLoading,
            onClick = {
                if (grade.isBlank()) {
                    errorMessage = "Grade is required"
                    return@ZenoButton
                }
                
                isLoading = true
                coroutineScope.launch {
                    val result = authRepository.completeData(
                        CompleteDataRequest(country = "EG", grade = grade.trim(), track = track.trim().takeIf { it.isNotBlank() })
                    )
                    isLoading = false
                    if (result.isSuccess) {
                        onSetupComplete()
                    } else {
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to save data"
                    }
                }
            }
        )
    }
}
