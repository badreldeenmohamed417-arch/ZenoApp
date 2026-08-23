package com.example.zeno.core.sections.setup.NameAge

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.core.theme.ButtonFun
import com.example.zeno.core.theme.TextFieldFun
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileMiddleSection(
    continueButton: (String, String) -> Unit
) {

    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }

    var isErrorName by remember { mutableStateOf(false) }
    var isErrorBirthDate by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    Column {

        TextFieldFun(
            text = name,
            placeholder = "Ahmed",
            label = txt("enterName"),
            isError = isErrorName,
            onTextChange = { newText ->
                name = newText
            }
        )

        Spacer(
            modifier = androidx.compose.ui.Modifier.height(12.dp)
        )

        TextFieldFun(
            text = birthDate,
            placeholder = "18/08/2010",
            label = txt("birthDate"),
            isError = isErrorBirthDate,
            onTextChange = {
                showDatePicker = true
            }
        )

        Spacer(
            modifier = androidx.compose.ui.Modifier.height(20.dp)
        )

        ButtonFun(
            onClick = {continueButton(name, birthDate)},
            items = {
                Text(
                    text = txt("continueButton"),
                    color = AppColors.Surface,
                    fontSize = 16.sp
                )
            }
        )
    }

    if (showDatePicker) {

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {

                TextButton(
                    onClick = {

                        datePickerState.selectedDateMillis?.let { millis ->

                            val formatter = SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            )

                            birthDate = formatter.format(
                                Date(millis)
                            )

                            isErrorBirthDate = false
                        }

                        showDatePicker = false
                    }
                ) {
                    Text("اختيار")
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("إلغاء")
                }
            }
        ) {

            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}