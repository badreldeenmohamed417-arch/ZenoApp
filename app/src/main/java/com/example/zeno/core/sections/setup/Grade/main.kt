package com.example.zeno.core.sections.setup.Grade

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import com.example.zeno.core.theme.ButtonFun
import com.example.zeno.core.txt
import com.example.zeno.core.sections.setup.SelectionGroupWidget
import com.example.zeno.data.AppColors

@Composable
fun GradeMiddleSection(
    continueButton: (String, String) -> Unit
) {
    var GradeOne by remember { mutableStateOf("") }
    var GradeTwo by remember { mutableStateOf("") }
    var GradeThree by remember { mutableStateOf("") }

    var selectedGrade by remember { mutableStateOf(GradeOne) }
    var selectedSystem by remember { mutableStateOf("") }
    var selectedSection by remember { mutableStateOf("") }

    Column {
        _root_ide_package_.com.example.zeno.core.sections.setup.SelectionGroupWidget(
            title = txt("gradeLabel"),
            options = listOf(GradeOne, GradeTwo, GradeThree),
            selectedOption = selectedGrade,
            onOptionSelected = { newSelection ->
                selectedGrade = newSelection
            }
        )

        _root_ide_package_.com.example.zeno.core.sections.setup.SelectionGroupWidget(
            title = txt("systemLabel"),
            options = listOf(txt("sectionAzhari"), txt("sectionElmi")),
            selectedOption = selectedSystem,
            onOptionSelected = { newSelection ->
                selectedSystem = newSelection
            }
        )

        when (selectedSystem) {
            txt("sectionElmi") -> {
                when (selectedGrade) {
                    GradeTwo -> {
                        _root_ide_package_.com.example.zeno.core.sections.setup.SelectionGroupWidget(
                            title = txt("trackLabel"),
                            options = listOf(
                                txt("trackMedicineLifeSciences"),
                                txt("trackEngineeringTech"),
                                txt("trackBusinessSocialSciences"),
                                txt("trackArtsHumanities")
                            ),
                            selectedOption = selectedSystem,
                            onOptionSelected = { newSelection ->
                                selectedSection = newSelection
                            }
                        )
                    }
                    GradeThree -> {
                        _root_ide_package_.com.example.zeno.core.sections.setup.SelectionGroupWidget(
                            title = txt("sectionLabel"),
                            options = listOf(
                                txt("sectionScientificScience"),
                                txt("sectionScientificMath"),
                                txt("sectionLiterary")
                            ),
                            selectedOption = selectedSection,
                            onOptionSelected = { newSelection ->
                                selectedSection = newSelection
                            }
                        )
                    }
                }
            }
            txt("sectionAzhari") -> {
                _root_ide_package_.com.example.zeno.core.sections.setup.SelectionGroupWidget(
                    title = txt("sectionLabel"),
                    options = listOf(txt("sectionScientific"), txt("sectionLiterary")),
                    selectedOption = selectedSection,
                    onOptionSelected = { newSelection ->
                        selectedSection = newSelection
                    }
                )
            }
        }

        ButtonFun(
            onClick = {continueButton(selectedGrade, selectedSystem)},
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