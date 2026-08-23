package com.example.zeno.core.sections.setup.NameAge

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.core.theme.ButtonFun
import com.example.zeno.core.theme.TextFieldFun
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ProfileMiddleSection(
    continueButton: (String, String) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var selectedMillis by remember { mutableStateOf<Long?>(null) }

    var isErrorName by remember { mutableStateOf(false) }
    var isErrorBirthDate by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // استدعاء النصوص Composable هنا بدلاً من داخل onClick
    val fillFieldsText = txt("fill_all_fields")
    val ageRestrictionText = txt("ageRestrictionError")
    val continueText = txt("Continue")
    val enterNameText = txt("enterName")
    val birthDateText = txt("birthDate")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextFieldFun(
            text = name,
            placeholder = "",
            label = enterNameText,
            isError = isErrorName,
            onTextChange = { newText ->
                name = newText
                if (isErrorName && newText.isNotBlank()) isErrorName = false
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box {
            TextFieldFun(
                text = birthDate,
                placeholder = "18/08/2010",
                label = birthDateText,
                isError = isErrorBirthDate,
                onTextChange = { }
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        ButtonFun(
            onClick = {
                val hasNameError = name.trim().isEmpty()
                val hasDateError = birthDate.trim().isEmpty()

                // حساب ما إذا كان العمر أقل من 13 سنة
                val isUnder13 = selectedMillis?.let { millis ->
                    val birthCalendar = Calendar.getInstance().apply { timeInMillis = millis }
                    val today = Calendar.getInstance()

                    var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
                    if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                        age--
                    }
                    age < 13
                } ?: false

                isErrorName = hasNameError
                isErrorBirthDate = hasDateError || isUnder13

                when {
                    hasNameError || hasDateError -> {
                        onError(fillFieldsText)
                    }
                    isUnder13 -> {
                        onError(ageRestrictionText)
                    }
                    else -> {
                        continueButton(name, birthDate)
                    }
                }
            },
            items = {
                Text(
                    text = continueText,
                    color = AppColors.Surface,
                    fontSize = 16.sp
                )
            }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedMillis = millis
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            birthDate = formatter.format(Date(millis))
                            isErrorBirthDate = false
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(txt("choose"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(txt("cancel"))
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