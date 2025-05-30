package com.example.ltdd.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val name: String?,
    @SerializedName("user_id") val userId: Int?
)