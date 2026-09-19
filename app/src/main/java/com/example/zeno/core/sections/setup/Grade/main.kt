package com.example.zeno.core.sections.setup.Grade

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.R
import com.example.zeno.core.sections.setup.SelectionGroupWidget
import com.example.zeno.core.theme.ButtonFun
import com.example.zeno.data.AppColors

@Composable
fun GradeMiddleSection(
    continueButton: (grade: String, schoolSystem: String, track: String) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val gradeOne = stringResource(R.string.gradeFirstSecondary)
    val gradeTwo = stringResource(R.string.gradeSecondSecondary)
    val gradeThree = stringResource(R.string.gradeThirdSecondary)

    val systemGeneral = stringResource(R.string.sectionElmi)
    val systemAzhari = stringResource(R.string.sectionAzhari)
    val systemBacc = stringResource(R.string.trackBaccalaureate)

    var selectedGrade by remember { mutableStateOf("") }
    var selectedSystem by remember { mutableStateOf("") }
    var selectedSection by remember { mutableStateOf("") }

    val isTrackRequired = (selectedGrade == gradeTwo || selectedGrade == gradeThree) &&
            (selectedSystem == systemBacc || selectedSystem == systemGeneral || selectedSystem == systemAzhari)

    val isFormValid = selectedGrade.isNotEmpty() &&
            selectedSystem.isNotEmpty() &&
            (!isTrackRequired || selectedSection.isNotEmpty())

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Grade / Academic Year
        SelectionGroupWidget(
            title = stringResource(R.string.gradeLabel),
            options = listOf(gradeTwo, gradeThree),
            selectedOption = selectedGrade.ifEmpty { null },
            onOptionSelected = { newSelection ->
                selectedGrade = newSelection
                selectedSystem = ""
                selectedSection = ""
            }
        )

        // 2. Educational System
        if (selectedGrade.isNotEmpty()) {
            val systemOptions = if (selectedGrade == gradeTwo) {
                listOf(systemBacc)
            } else {
                listOf(systemGeneral, systemAzhari)
            }
            SelectionGroupWidget(
                title = stringResource(R.string.systemLabel),
                options = systemOptions,
                selectedOption = selectedSystem.ifEmpty { null },
                onOptionSelected = { newSelection ->
                    selectedSystem = newSelection
                    selectedSection = ""
                }
            )
        }

        // 3. Track / Section
        AnimatedVisibility(
            visible = selectedSystem.isNotEmpty() && selectedGrade.isNotEmpty() && isTrackRequired,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            when {
                selectedSystem == systemBacc -> {
                    SelectionGroupWidget(
                        title = stringResource(R.string.trackLabel),
                        options = listOf(
                            stringResource(R.string.trackMedicineLifeSciences),
                            stringResource(R.string.trackEngineeringTech),
                            stringResource(R.string.trackBusinessSocialSciences),
                            stringResource(R.string.trackArtsHumanities)
                        ),
                        selectedOption = selectedSection.ifEmpty { null },
                        onOptionSelected = { selectedSection = it }
                    )
                }
                selectedGrade == gradeTwo -> {
                    SelectionGroupWidget(
                        title = stringResource(R.string.sectionLabel),
                        options = listOf(
                            stringResource(R.string.sectionScientific),
                            stringResource(R.string.sectionLiterary)
                        ),
                        selectedOption = selectedSection.ifEmpty { null },
                        onOptionSelected = { selectedSection = it }
                    )
                }
                selectedGrade == gradeThree && selectedSystem == systemGeneral -> {
                    SelectionGroupWidget(
                        title = stringResource(R.string.sectionLabel),
                        options = listOf(
                            stringResource(R.string.sectionScientificScience),
                            stringResource(R.string.sectionScientificMath),
                            stringResource(R.string.sectionLiterary)
                        ),
                        selectedOption = selectedSection.ifEmpty { null },
                        onOptionSelected = { selectedSection = it }
                    )
                }
                selectedGrade == gradeThree && selectedSystem == systemAzhari -> {
                    SelectionGroupWidget(
                        title = stringResource(R.string.sectionLabel),
                        options = listOf(
                            stringResource(R.string.sectionScientific),
                            stringResource(R.string.sectionLiterary)
                        ),
                        selectedOption = selectedSection.ifEmpty { null },
                        onOptionSelected = { selectedSection = it }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ButtonFun(
            onClick = { continueButton(selectedGrade, selectedSystem, selectedSection) },
            enabled = isFormValid && !isLoading,
            items = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = AppColors.AccentInk,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.setupCta),
                        color = AppColors.AccentInk,
                        fontSize = 16.sp
                    )
                }
            }
        )
    }
}
