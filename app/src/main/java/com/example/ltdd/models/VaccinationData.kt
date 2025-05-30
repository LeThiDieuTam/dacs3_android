package com.example.ltdd.models

data class VaccinationData(
    val id: Int,
    val vaccineName: String,
    val vaccinationDate: String,
    val notes: String,
    val location: String,
    var isInjected: Boolean
)