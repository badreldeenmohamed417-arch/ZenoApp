package com.example.zeno.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val title: String?,
    val subjectId: String?,
    val updatedAt: String,
    val lastMessageAt: String
)
