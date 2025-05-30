package com.example.ltdd.remote

import com.example.ltdd.models.GrowthModel
import retrofit2.http.GET
import retrofit2.http.Query

interface GrowthService {
    @GET("get_pregnancy_info.php")
    suspend fun getGrowthInfo(@Query("user_id") userId: String): List<GrowthModel>
}