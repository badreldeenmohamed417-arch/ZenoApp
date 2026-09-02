package com.example.zeno.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.zeno.R
import com.example.zeno.core.theme.ErrorRed
import com.example.zeno.core.ui.modifiers.bounceClickable

@Composable
fun AccountDataScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.settings_account),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = stringResource(id = R.string.account_email),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ahmed@example.com",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(id = R.string.account_change_password))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(id = R.string.account_export_data))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Danger Zone",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = ErrorRed
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Danger Zone
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClickable { /* TODO */ },
                colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.account_delete_all_chats),
                        style = MaterialTheme.typography.titleMedium,
                        color = ErrorRed,
                        modifier = Modifier.padding(16.dp).fillMaxWidth()
                    )
                    Divider(color = ErrorRed.copy(alpha = 0.1f))
                    Text(
                        text = stringResource(id = R.string.account_delete_all_data),
                        style = MaterialTheme.typography.titleMedium,
                        color = ErrorRed,
                        modifier = Modifier.padding(16.dp).fillMaxWidth()
                    )
                    Divider(color = ErrorRed.copy(alpha = 0.1f))
                    Text(
                        text = stringResource(id = R.string.account_delete),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ErrorRed,
                        modifier = Modifier.padding(16.dp).fillMaxWidth()
                    )
                }
            }
        }
    }
}
