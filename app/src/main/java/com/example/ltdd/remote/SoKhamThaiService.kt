package com.example.ltdd.remote

import com.example.ltdd.models.ApiResponse
import com.example.ltdd.models.Visit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SoKhamThaiService {

    @GET("so_kham_thai_get.php")
    suspend fun getVisits(@Query("user_id") userId: String): List<Visit>

    @FormUrlEncoded
    @POST("so_kham_thai_add.php")
    suspend fun addVisit(
        @Field("user_id") userId: String,
        @Field("visit_date") visitDate: String,
        @Field("doctor_name") doctorName: String,
        @Field("gestational_age") gestationalAge: String,
        @Field("weight") weight: String,
        @Field("blood_pressure") bloodPressure: String,
        @Field("fetal_heart_rate") fetalHeartRate: String,
        @Field("notes") notes: String?
    ): ApiResponse

    @FormUrlEncoded
    @POST("so_kham_thai_update.php")
    suspend fun updateVisit(
        @Field("visit_id") visitId: String,
        @Field("user_id") userId: String,
        @Field("visit_date") visitDate: String,
        @Field("doctor_name") doctorName: String,
        @Field("gestational_age") gestationalAge: String,
        @Field("weight") weight: String,
        @Field("blood_pressure") bloodPressure: String,
        @Field("fetal_heart_rate") fetalHeartRate: String,
        @Field("notes") notes: String?
    ): ApiResponse

    @FormUrlEncoded
    @POST("so_kham_thai_delete.php")
    suspend fun deleteVisit(
        @Field("user_id") userId: String,
        @Field("visit_id") visitId: String
    ): ApiResponse
}