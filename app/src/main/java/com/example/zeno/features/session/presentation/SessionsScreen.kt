package com.example.zeno.features.session.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.zeno.R
import com.example.zeno.features.session.domain.SessionStatus
import com.example.zeno.features.session.domain.StudySession

import com.example.zeno.features.session.presentation.SessionsViewModel
import com.example.zeno.features.session.presentation.SessionsUiState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.zeno.features.session.data.dto.StudySessionDTO

@Composable
fun SessionsScreen(viewModel: SessionsViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(R.string.sessions_tab_upcoming, R.string.sessions_tab_completed)
    
    val uiState by viewModel.uiState.collectAsState()
    
    val currentList = when (uiState) {
        is SessionsUiState.Success -> {
            val sessions = (uiState as SessionsUiState.Success).sessions
            if (selectedTabIndex == 0) {
                sessions.filter { it.status != "COMPLETED" }
            } else {
                sessions.filter { it.status == "COMPLETED" }
            }
        }
        else -> emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.sessions_title),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
                
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, titleRes ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { 
                                Text(
                                    text = stringResource(id = titleRes),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        )
                    }
                }
            }
        }

        // List
        val displayList = currentList.map { dto ->
            StudySession(
                title = dto.title,
                subject = dto.subject,
                status = when(dto.status.uppercase()) {
                    "PENDING" -> SessionStatus.PENDING
                    "IN_PROGRESS" -> SessionStatus.IN_PROGRESS
                    "COMPLETED" -> SessionStatus.COMPLETED
                    else -> SessionStatus.PENDING
                }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(displayList) { session ->
                SessionCard(session = session)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SessionCard(session: StudySession) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = session.subject,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                Text(
                    text = when(session.status) {
                        SessionStatus.PENDING -> stringResource(id = R.string.session_status_pending)
                        SessionStatus.IN_PROGRESS -> stringResource(id = R.string.session_status_in_progress)
                        SessionStatus.COMPLETED -> stringResource(id = R.string.session_status_completed)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = when(session.status) {
                        SessionStatus.COMPLETED -> Color(0xFF4CAF50)
                        SessionStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = session.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            
            if (session.status != SessionStatus.COMPLETED) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* TODO: Start/Continue Session */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (session.status == SessionStatus.IN_PROGRESS) 
                            stringResource(id = R.string.session_action_continue) 
                        else 
                            stringResource(id = R.string.session_action_start),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
