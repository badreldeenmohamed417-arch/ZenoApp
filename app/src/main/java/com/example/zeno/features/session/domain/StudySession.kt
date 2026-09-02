package com.example.zeno.features.session.domain

import java.util.UUID

enum class SessionStatus {
    PENDING, IN_PROGRESS, COMPLETED
}

data class StudySession(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subject: String,
    val status: SessionStatus = SessionStatus.PENDING,
    val date: Long = System.currentTimeMillis()
)
