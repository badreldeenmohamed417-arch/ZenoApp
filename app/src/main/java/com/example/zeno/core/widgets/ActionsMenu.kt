package com.example.zeno.core.widgets

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.zeno.data.AppColors

data class ActionMenuItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun ActionMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<ActionMenuItem>
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        items.forEach { item ->

            DropdownMenuItem(
                text = {
                    Row {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = item.title,
                            color = AppColors.TextPrimary
                        )
                    }
                },
                onClick = {
                    item.onClick()
                    onDismissRequest()
                }
            )
        }
    }
}