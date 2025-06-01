package com.example.ltdd.remote


import com.example.ltdd.models.ApiResponse
import com.example.ltdd.models.BirthPlanQuestion
import com.example.ltdd.models.BirthPlanSelectionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BirthPlanApiService {
    @GET("birth_plan_questions.php")
    suspend fun getBirthPlanQuestions(): List<BirthPlanQuestion>
    @POST("save_birth_plan_selection.php")
    suspend fun saveBirthPlanSelection(@Body request: BirthPlanSelectionRequest): Response<ApiResponse>
}