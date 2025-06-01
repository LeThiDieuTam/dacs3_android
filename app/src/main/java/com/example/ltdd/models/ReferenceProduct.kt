package com.example.ltdd.models


import com.google.gson.annotations.SerializedName

data class ReferenceProduct(
    val id: Int,
    val name: String,
    val description: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("created_at") val createdAt: String?
)