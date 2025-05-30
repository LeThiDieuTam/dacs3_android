package com.example.ltdd.remote

import com.example.ltdd.models.VaccinationData
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface VaccinationApiService {
    @GET("getVaccinations.php")
    suspend fun getVaccinations(@Query("userId") userId: String): List<VaccinationData>

    @FormUrlEncoded
    @POST("updateVaccination.php")
    suspend fun updateVaccinationInjected(
        @Field("userId") userId: String,
        @Field("id") shotId: Int,
        @Field("isInjected") isInjected: Int
    )
}