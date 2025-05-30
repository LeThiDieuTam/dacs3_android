package com.example.ltdd.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.example.ltdd.remote.FoodPostService
import com.example.ltdd.remote.SoKhamThaiService
import com.example.ltdd.remote.LoginService
import com.example.ltdd.remote.GrowthService
import com.example.ltdd.remote.VaccinationApiService
import com.example.ltdd.remote.ActivityApiService

object RetrofitClient {

    private const val BASE_URL = "https://cd89-2001-ee0-4b6d-f0a0-bc15-4b82-50c3-ee65.ngrok-free.app/mevabe_api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: SoKhamThaiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(SoKhamThaiService::class.java)
    }

    // Service cho Đăng nhập/Xác thực
    val authInstance: LoginService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(LoginService::class.java)
    }

    // Service cho Bài viết về Thực phẩm
    val foodPostService: FoodPostService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(FoodPostService::class.java)
    }

    // Service cho Dữ liệu tăng trưởng (GrowthScreen)
    val growthService: GrowthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(GrowthService::class.java)
    }

    val vaccinationInstance: VaccinationApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(VaccinationApiService::class.java)
    }


    val vaccinationUpdateInstance: VaccinationApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(VaccinationApiService::class.java)
    }


    val activityInstance: ActivityApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ActivityApiService::class.java)
    }
}