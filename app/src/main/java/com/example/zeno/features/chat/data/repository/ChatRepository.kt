package com.example.zeno.features.chat.data.repository

import com.example.zeno.features.chat.data.ChatApi
import com.example.zeno.features.chat.data.dto.*
import com.example.zeno.core.data.AuthStorage
import com.example.zeno.data.local.db.AppDatabase
import com.example.zeno.data.local.db.ConversationEntity
import com.example.zeno.data.local.db.MessageEntity
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.*
import retrofit2.HttpException

class ChatRepository(
    private val chatApi: ChatApi,
    private val authStorage: AuthStorage,
    private val database: AppDatabase
) {
    private val dao = database.chatDao()
    private val gson = Gson()
    private val httpClient = OkHttpClient()
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private suspend fun saveConversationLocal(item: ConversationResponse) {
        dao.insertConversations(
            listOf(
                ConversationEntity(
                    id = item.id,
                    title = item.title,
                    subjectId = item.subjectId,
                    updatedAt = item.updatedAt,
                    lastMessageAt = item.lastMessageAt
                )
            )
        )
    }

    private suspend fun saveMessagesLocal(conversationId: String, messages: List<MessageResponse>) {
        dao.insertMessages(messages.map {
            MessageEntity(
                id = it.id,
                conversationId = conversationId,
                role = it.role,
                content = it.content,
                createdAt = it.createdAt
            )
        })
    }

    suspend fun getConversations(): Result<ConversationListResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.getConversations()
            val remoteIds = response.items.map { it.id }.toSet()
            val localIds = dao.getConversationIds()
            localIds.filterNot(remoteIds::contains).forEach {
                dao.deleteMessages(it)
                dao.deleteConversation(it)
            }
            dao.insertConversations(response.items.map {
                ConversationEntity(it.id, it.title, it.subjectId, it.updatedAt, it.lastMessageAt)
            })
            Result.success(response)
        } catch (e: Exception) {
            val local = dao.getConversations().first()
            Result.success(
                ConversationListResponse(
                    local.map {
                        ConversationResponse(
                            it.id, it.title, it.subjectId, "", it.updatedAt, it.lastMessageAt, false
                        )
                    }
                )
            )
        }
    }

    suspend fun getConversationDetails(id: String): Result<ConversationDetailResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.getConversation(id)
            saveConversationLocal(response)
            dao.deleteMessages(id)
            saveMessagesLocal(id, response.messages)
            Result.success(response)
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 404) {
                dao.deleteMessages(id)
                dao.deleteConversation(id)
                return@withContext Result.failure(e)
            }
            val local = dao.getMessages(id).first()
            if (local.isNotEmpty()) {
                val messages = local.map {
                    MessageResponse(it.id, id, it.role, it.content, it.createdAt)
                }
                return@withContext Result.success(
                    ConversationDetailResponse(id, null, null, "", "", "", false, messages)
                )
            }
            Result.failure(e)
        }
    }

    suspend fun sendConversationMessage(id: String, content: String): Result<MessageResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.sendConversationMessage(id, SendMessageRequest(content = content))
            dao.insertMessage(MessageEntity(response.id, id, response.role, response.content, response.createdAt))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun openWebSocket(
        conversationId: String,
        content: String,
        onProgress: (String) -> Unit,
        onCompleted: (MessageResponse) -> Unit,
        onError: (Throwable) -> Unit,
        onStarted: ((String) -> Unit)? = null
    ): WebSocket {
        val token = authStorage.getToken()
        val request = Request.Builder()
            .url("wss://zenohostingserver.fastapicloud.dev/main/chat/ws")
            .apply {
                if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
            }
            .build()

        val listener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val json = gson.fromJson(text, JsonObject::class.java)
                    when (json.get("type")?.asString) {
                        "started" -> onStarted?.invoke(json.get("conversation_id")?.asString ?: conversationId)
                        "progress" -> onProgress(json.get("stage")?.asString ?: "thinking")
                        "completed" -> {
                            val msg = gson.fromJson(json.getAsJsonObject("message"), MessageResponse::class.java)
                            ioScope.launch {
                                dao.insertMessage(MessageEntity(msg.id, msg.conversationId, msg.role, msg.content, msg.createdAt))
                            }
                            onCompleted(msg)
                        }
                        "error" -> onError(RuntimeException(json.get("message")?.asString ?: "request_failed"))
                    }
                } catch (e: Exception) {
                    onError(e)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                onError(t)
            }
        }
        val ws = httpClient.newWebSocket(request, listener)
        val payload = gson.toJson(mapOf("conversation_id" to conversationId, "message" to content))
        ws.send(payload)
        return ws
    }

    suspend fun updateConversationTitle(id: String, title: String): Result<ConversationResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApi.updateConversationTitle(id, UpdateConversationRequest(title = title))
            saveConversationLocal(response)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteConversation(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            chatApi.deleteConversation(id)
            dao.deleteMessages(id)
            dao.deleteConversation(id)
            Result.success(Unit)
        } catch (e: HttpException) {
            if (e.code() == 404) {
                dao.deleteMessages(id)
                dao.deleteConversation(id)
                Result.success(Unit)
            } else {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearLocalChats() = withContext(Dispatchers.IO) {
        dao.clearMessages()
        dao.clearConversations()
    }

    suspend fun feedbackMessage(id: String, feedback: String): Result<FeedbackResponse> = withContext(Dispatchers.IO) {
        try {
            Result.success(chatApi.feedbackMessage(id, MessageFeedbackRequest(feedback)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun reportMessage(id: String, reason: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            chatApi.reportMessage(id, ReportRequest(reason = reason))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
