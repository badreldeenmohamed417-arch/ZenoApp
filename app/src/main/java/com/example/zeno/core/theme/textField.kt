package com.example.zeno.core.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.zeno.data.AppColors

@Composable
fun TextFieldFun(
    text: String,
    onTextChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    isError: Boolean = false,
    isPassword: Boolean = false,
    cornerRadius: Int = 20,
    focusedTextColor: Color = AppColors.Black,
    unfocusedTextColor: Color = AppColors.Black
) {
    // حالة للتحكم في إظهار أو إخفاء كلمة المرور
    var isPasswordVisible by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            label = {
                Text(label)
            },
            placeholder = {
                Text(placeholder)
            },
            // التبديل بين تشفير النص وإظهاره بناءً على اختيار المستخدم
            visualTransformation = if (isPassword && !isPasswordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            // إظهار أيقونة العين فقط إذا كان الحقل كلمة مرور
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                            tint = AppColors.Black.copy(alpha = 0.6f)
                        )
                    }
                }
            } else null,
            isError = isError,
            singleLine = true,
            shape = RoundedCornerShape(cornerRadius.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = focusedTextColor,
                unfocusedTextColor = unfocusedTextColor,

                focusedBorderColor = AppColors.FocusedBorder,
                unfocusedBorderColor = AppColors.UnfocusedBorder,
                focusedContainerColor = AppColors.Surface,
                unfocusedContainerColor = AppColors.Surface,

                focusedPlaceholderColor = AppColors.Black.copy(alpha = 0.5f),
                unfocusedPlaceholderColor = AppColors.Black.copy(alpha = 0.4f),

                focusedLabelColor = AppColors.Black,
                unfocusedLabelColor = AppColors.Black.copy(0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}