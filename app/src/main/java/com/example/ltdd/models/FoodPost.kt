package com.example.ltdd.models

import com.google.gson.annotations.SerializedName
data class FoodPost(
    val id: Int,
    val name: String,
    @SerializedName("image_url") val imageUrl: String?,
    val category: String?,
    val description: String?,
    val nutrition: String?,
    val benefits: String?,
    @SerializedName("created_at") val createdAt: String
)