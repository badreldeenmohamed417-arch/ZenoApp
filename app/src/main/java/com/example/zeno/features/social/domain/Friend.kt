package com.example.zeno.features.social.domain

data class Friend(
    val id: String = "",
    val name: String,
    val xp: Int,
    val streak: Int,
    val isRequest: Boolean = false
)
