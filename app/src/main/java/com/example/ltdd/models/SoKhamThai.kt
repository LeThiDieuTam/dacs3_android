package com.example.ltdd.models


import com.google.gson.annotations.SerializedName


data class Visit(
    @SerializedName("visit_id") val visitId: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("visit_date") val visitDate: String,
    @SerializedName("doctor_name") val doctorName: String,
    @SerializedName("gestational_age") val gestationalAge: String,
    @SerializedName("weight") val weight: String,
    @SerializedName("blood_pressure") val bloodPressure: String,
    @SerializedName("fetal_heart_rate") val fetalHeartRate: String,
    @SerializedName("notes") val notes: String?
)

data class ApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)