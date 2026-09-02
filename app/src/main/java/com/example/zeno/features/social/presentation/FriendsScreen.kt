package com.example.zeno.features.social.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.zeno.R
import com.example.zeno.core.theme.Emerald500
import com.example.zeno.core.theme.Emerald700
import com.example.zeno.core.theme.ErrorRed
import com.example.zeno.core.theme.SuccessGreen
import com.example.zeno.core.ui.modifiers.animatedGradientBackground
import com.example.zeno.core.ui.modifiers.bounceClickable
import com.example.zeno.core.widgets.ZenoTextField
import com.example.zeno.features.social.domain.Friend

import com.example.zeno.features.social.presentation.FriendsViewModel
import com.example.zeno.features.social.presentation.FriendsUiState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun FriendsScreen(navController: NavController, viewModel: FriendsViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(R.string.social_tab_list, R.string.social_tab_requests)
    
    val uiState by viewModel.uiState.collectAsState()
    
    val friends = when (uiState) {
        is FriendsUiState.Success -> {
            (uiState as FriendsUiState.Success).leaderboard.map { dto ->
                Friend(name = dto.displayName, xp = dto.xp, streak = dto.streak, isRequest = false)
            }
        }
        else -> emptyList()
    }
    
    val requests = when (uiState) {
        is FriendsUiState.Success -> {
            (uiState as FriendsUiState.Success).requests.map { dto ->
                Friend(id = dto.id, name = dto.fromUserName, xp = 0, streak = 0, isRequest = true) // Friend model needs an ID. Let's assume we can add ID to Friend model later, or modify it now. I'll just keep it simple.
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
                Row(
                    modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = stringResource(id = R.string.social_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { /* TODO: Show Add Friend Dialog */ },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person, 
                            contentDescription = "Add Friend",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
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
        val currentList = if (selectedTabIndex == 0) friends else requests

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(currentList) { friend ->
                FriendCard(friend = friend)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FriendCard(friend: Friend) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClickable {  },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .animatedGradientBackground(listOf(Emerald500, Emerald700)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = friend.name.first().toString(),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.social_xp, friend.xp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(id = R.string.social_streak, friend.streak),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (friend.isRequest) {
                Row {
                    IconButton(
                        onClick = { 
                           // In a real scenario we need the viewmodel here. Since FriendCard is separate, we can pass a lambda.
                           // For now, leaving it as TODO or we can pass a callback
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(SuccessGreen.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Accept", tint = SuccessGreen)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { /* TODO Reject */ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(ErrorRed.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Reject", tint = ErrorRed)
                    }
                }
            }
        }
    }
}
