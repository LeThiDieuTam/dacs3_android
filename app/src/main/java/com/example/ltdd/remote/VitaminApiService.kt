package com.example.ltdd.remote

import com.example.ltdd.models.Vitamin
import retrofit2.http.GET

interface VitaminApiService {
    @GET("vitamins.php")
    suspend fun getVitamins(): List<Vitamin>
}