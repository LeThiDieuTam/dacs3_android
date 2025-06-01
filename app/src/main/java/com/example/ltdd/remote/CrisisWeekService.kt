package com.example.ltdd.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.ltdd.models.CrisisWeek

interface CrisisWeekService {
    @GET("get_crisis_week_info.php")
    suspend fun getCrisisWeekInfo(@Query("user_id") userId: String): List<CrisisWeek>
}