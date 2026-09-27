package com.example.zeno.data.local.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM conversations ORDER BY lastMessageAt DESC")
    fun getConversations(): Flow<List<ConversationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<ConversationEntity>)

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    fun getMessages(conversationId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteMessages(conversationId: String)

    @Query("DELETE FROM conversations WHERE id = :conversationId")
    suspend fun deleteConversation(conversationId: String)

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)

    @Query("DELETE FROM messages WHERE conversationId = :conversationId AND role = 'user' AND content = :content")
    suspend fun deleteMessageByConversationAndContent(conversationId: String, content: String)

    @Query("SELECT id FROM conversations")
    suspend fun getConversationIds(): List<String>

    @Query("DELETE FROM conversations")
    suspend fun clearConversations()

    @Query("DELETE FROM messages")
    suspend fun clearMessages()
}
