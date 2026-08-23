package com.example.zeno.core.sections.setup.Grade

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.core.sections.setup.SelectionGroupWidget
import com.example.zeno.core.theme.ButtonFun
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors

@Composable
fun GradeMiddleSection(
    continueButton: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val gradeOne = txt("gradeFirstSecondary")
    val gradeTwo = txt("gradeSecondSecondary")
    val gradeThree = txt("gradeThirdSecondary")

    var selectedGrade by remember { mutableStateOf("") }
    var selectedSystem by remember { mutableStateOf("") }
    var selectedSection by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SelectionGroupWidget(
                title = txt("gradeLabel"),
                options = listOf(gradeOne, gradeTwo, gradeThree),
                selectedOption = selectedGrade.ifEmpty { null },
                onOptionSelected = { newSelection ->
                    selectedGrade = newSelection
                    selectedSection = ""
                }
            )

            SelectionGroupWidget(
                title = txt("systemLabel"),
                options = listOf(txt("sectionAzhari"), txt("sectionElmi")),
                selectedOption = selectedSystem.ifEmpty { null },
                onOptionSelected = { newSelection ->
                    selectedSystem = newSelection
                    selectedSection = ""
                }
            )

            AnimatedVisibility(
                visible = selectedSystem.isNotEmpty() && selectedGrade.isNotEmpty(),
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                when (selectedSystem) {
                    txt("sectionElmi") -> {
                        when (selectedGrade) {
                            gradeTwo -> {
                                SelectionGroupWidget(
                                    title = txt("trackLabel"),
                                    options = listOf(
                                        txt("trackMedicineLifeSciences"),
                                        txt("trackEngineeringTech"),
                                        txt("trackBusinessSocialSciences"),
                                        txt("trackArtsHumanities")
                                    ),
                                    selectedOption = selectedSection.ifEmpty { null },
                                    onOptionSelected = { selectedSection = it }
                                )
                            }
                            gradeThree -> {
                                SelectionGroupWidget(
                                    title = txt("sectionLabel"),
                                    options = listOf(
                                        txt("sectionScientificScience"),
                                        txt("sectionScientificMath"),
                                        txt("sectionLiterary")
                                    ),
                                    selectedOption = selectedSection.ifEmpty { null },
                                    onOptionSelected = { selectedSection = it }
                                )
                            }
                        }
                    }
                    txt("sectionAzhari") -> {
                        SelectionGroupWidget(
                            title = txt("sectionLabel"),
                            options = listOf(txt("sectionScientific"), txt("sectionLiterary")),
                            selectedOption = selectedSection.ifEmpty { null },
                            onOptionSelected = { selectedSection = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        ButtonFun(
            onClick = { continueButton(selectedGrade, selectedSystem) },
            enabled = selectedGrade.isNotEmpty() && selectedSystem.isNotEmpty(),
            items = {
                Text(
                    text = txt("setupCta"),
                    color = AppColors.Surface,
                    fontSize = 16.sp
                )
            }
        )
    }
}