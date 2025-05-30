package com.example.ltdd.remote

import com.example.ltdd.models.ActivityResponse
import com.example.ltdd.models.CompletionResponse
import com.example.ltdd.models.ActivityDayIndexResponse
import com.example.ltdd.models.SaveActivityResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ActivityApiService {

    @GET("get_daily_activities.php")
    suspend fun getDailyActivities(
        @Query("user_id") userId: String,
        @Query("activity_date") activityDate: String
    ): List<ActivityResponse>

    @FormUrlEncoded
    @POST("save_activity.php")
    suspend fun saveActivity(
        @Field("action") action: String,
        @Field("user_id") userId: String,
        @Field("activity_title") activityTitle: String,
        @Field("description") description: String,
        @Field("is_done") isDone: Int,
        @Field("activity_date") activityDate: String
    ): SaveActivityResponse

    @GET("get_completion.php")
    suspend fun getCompletion(
        @Query("user_id") userId: String,
        @Query("activity_date") activityDate: String
    ): CompletionResponse

    @GET("get_activity_day_index.php")
    suspend fun getActivityDayIndex(
        @Query("user_id") userId: String,
        @Query("activity_date") activityDate: String
    ): ActivityDayIndexResponse
}