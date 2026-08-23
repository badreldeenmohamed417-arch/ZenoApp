package com.example.zeno.data.model.server

import com.google.gson.annotations.SerializedName

data class Subject(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String? = null
)
