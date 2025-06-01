package com.example.ltdd.models

data class BirthPlanOption(
    val id: String,
    val text: String,
    var isSelected: Boolean = false
)

data class BirthPlanQuestion(
    val id: String,
    val title: String,
    val options: List<BirthPlanOption>
)