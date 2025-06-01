package com.example.ltdd.remote

import com.example.ltdd.models.ReferenceProduct
import retrofit2.http.GET

interface ReferenceProductService {
    @GET("get_reference_product.php")
    suspend fun getReferenceProducts(): List<ReferenceProduct>

}