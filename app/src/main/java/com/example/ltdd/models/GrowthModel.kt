package com.example.ltdd.models

import com.google.gson.annotations.SerializedName

data class GrowthModel(
    val id: Int,
    @SerializedName("user_id") val userId: Int?,
    val week: String?,
    val weight: Float?,
    val length: Float?,
    val image: String?,
    val description: String?,
    @SerializedName("due_date") val dueDate: String?,
    @SerializedName("created_at") val createdAt: String?
)