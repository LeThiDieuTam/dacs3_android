package com.example.ltdd.models


import com.google.gson.annotations.SerializedName

data class BirthPlanSelectionRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("question_id") val questionId: String,
    @SerializedName("option_id") val optionId: String,
    @SerializedName("is_selected") val isSelected: Boolean
)

data class ApiResponse1(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)