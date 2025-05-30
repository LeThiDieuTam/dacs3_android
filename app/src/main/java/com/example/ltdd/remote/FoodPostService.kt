package com.example.ltdd.remote


import com.example.ltdd.models.FoodPost
import retrofit2.http.GET

interface FoodPostService {
    @GET("get_nutrition_posts.php")
    suspend fun getFoodPosts(): List<FoodPost>
}