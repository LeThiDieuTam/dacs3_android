package com.example.ltdd.models


import com.google.gson.annotations.SerializedName

data class CrisisWeek(
    val id: Int?,
    @SerializedName("user_id") val userId: Int?,
    val week: Int?,
    val title: String?,
    val description: String?,
    @SerializedName("created_at") val createdAt: String?
)