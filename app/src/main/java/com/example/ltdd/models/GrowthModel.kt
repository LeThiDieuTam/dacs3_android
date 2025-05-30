package com.example.ltdd.models

data class GrowthModel(
    val id: Int,
    val userId: Int?,
    val week: String?,
    val weight: String?,
    val length: String?,
    val image: String?,
    val description: String?,
    val dueDate: String?,
    val createdAt: String?
)