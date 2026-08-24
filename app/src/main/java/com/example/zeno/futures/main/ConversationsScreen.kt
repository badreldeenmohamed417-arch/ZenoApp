package com.example.zeno.futures.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors
import com.example.zeno.data.local.db.AppDatabase
import com.example.zeno.data.model.server.ConversationResponse
import com.example.zeno.data.repository.ChatRepository
import com.example.zeno.core.widgets.BottomNavItem
import com.example.zeno.core.widgets.ZenoBottomNavigationBar
import com.example.zeno.futures.session.Orb

@Composable
fun ConversationsScreen(
    onBack: () -> Unit,
    onHome: () -> Unit,
    onSettings: () -> Unit,
    onNewChat: () -> Unit,
    onConversationClick: (String) -> Unit
) {
    val context = LocalContext.current
    val chatRepository = remember {
        ChatRepository(AppDatabase.getDatabase(context).chatDao())
    }
    
    var searchQuery by remember { mutableStateOf("") }
    val localConversations by chatRepository.getLocalConversations().collectAsStateWithLifecycle(initialValue = emptyList())
    val filteredConversations = localConversations.filter { 
        it.title?.contains(searchQuery, ignoreCase = true) ?: true 
    }

    LaunchedEffect(Unit) {
        try {
            chatRepository.getConversations()
        } catch (e: Exception) {
            // Error fetching
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BG)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Header(onBack, onNewChat)

            SearchBar(searchQuery) { searchQuery = it }

            if (filteredConversations.isEmpty()) {
                EmptyConversationsState()
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredConversations, key = { it.id }) { conv ->
                        ConversationCard(conv, onConversationClick)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            ZenoBottomNavigationBar(
                selectedTab = BottomNavItem.CHATS,
                onTabSelected = { item ->
                    when (item) {
                        BottomNavItem.HOME -> onHome()
                        BottomNavItem.SETTINGS -> onSettings()
                        else -> {}
                    }
                }
            )
        }
    }
}

@Composable
private fun Header(onBack: () -> Unit, onNewChat: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp, 20.dp, 18.dp, 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = txt("conversationsTitle"),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
        
        IconButton(
            onClick = onNewChat,
            modifier = Modifier.size(36.dp).background(AppColors.AccentSoft, CircleShape)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = AppColors.Accent, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .fillMaxWidth(),
        placeholder = { Text(txt("conversationsSearchHint"), color = AppColors.TextFaint, fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AppColors.TextFaint, modifier = Modifier.size(20.dp)) },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = AppColors.SurfaceVariant,
            unfocusedContainerColor = AppColors.SurfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = AppColors.TextPrimary,
            unfocusedTextColor = AppColors.TextPrimary
        ),
        singleLine = true
    )
}

@Composable
fun ConversationCard(conv: ConversationResponse, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.Surface)
            .border(1.dp, AppColors.UnfocusedBorder, RoundedCornerShape(12.dp))
            .clickable { onClick(conv.id) }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AppColors.AccentSoft),
            contentAlignment = Alignment.Center
        ) {
            Orb(modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = conv.title ?: txt("untitled"), fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
            Text(text = txt("lastMessagePlaceholder"), fontSize = 12.5.sp, color = AppColors.TextMuted, maxLines = 1)
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = txt("timeHoursAgo"), fontSize = 11.sp, color = AppColors.TextFaint)
            IconButton(onClick = { /* Delete */ }, modifier = Modifier.size(16.dp)) {
                Icon(Icons.Default.Close, contentDescription = null, tint = AppColors.TextFaint)
            }
        }
    }
}

@Composable
fun EmptyConversationsState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Orb(modifier = Modifier.size(72.dp))
        Spacer(modifier = Modifier.height(18.dp))
        Text(text = txt("conversationsEmptyTitle"), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = txt("conversationsEmptySubtitle"), fontSize = 13.5.sp, color = AppColors.TextMuted)
    }
}
