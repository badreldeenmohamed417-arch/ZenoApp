package com.example.zeno.futures.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeno.core.txt
import com.example.zeno.data.AppColors
import com.example.zeno.data.model.server.ConversationResponse
import com.example.zeno.futures.session.Orb

@Composable
fun ConversationsScreen(
    onBack: () -> Unit,
    onConversationClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    // Mock conversations
    val conversations = remember { mutableStateListOf<ConversationResponse>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BG)
    ) {
        Header(onBack)

        SearchBar(searchQuery) { searchQuery = it }

        if (conversations.isEmpty()) {
            EmptyConversationsState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(conversations) { conv ->
                    ConversationCard(conv, onConversationClick)
                }
            }
        }
    }
}

@Composable
private fun Header(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp, 20.dp, 18.dp, 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = txt("conversationsTitle"),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.SurfaceVariant)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Search, contentDescription = null, tint = AppColors.TextFaint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(txt("conversationsSearchHint"), color = AppColors.TextFaint, fontSize = 14.sp)
                    }
                    innerTextField()
                }
            )
        }
    }
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
            Text(text = conv.title ?: "بدون عنوان", fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
            Text(text = "آخر رسالة...", fontSize = 12.5.sp, color = AppColors.TextMuted, maxLines = 1)
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "منذ ساعتين", fontSize = 11.sp, color = AppColors.TextFaint)
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
