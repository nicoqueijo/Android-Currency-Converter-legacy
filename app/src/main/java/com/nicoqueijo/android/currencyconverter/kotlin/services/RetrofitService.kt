package com.nicoqueijo.android.currencyconverter.kotlin.services

import com.nicoqueijo.android.currencyconverter.kotlin.models.ApiEndPoint
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// https://openexchangerates.org/api/latest.json?app_id={app_id}

interface RetrofitService {

    @GET("api/latest.json")
    suspend fun getExchangeRates(@Query("app_id") app_id: String): Response<ApiEndPoint>
}

object RetrofitFactory {
    const val BASE_URL = "https://openexchangerates.org/"

    fun getRetrofitService(): RetrofitService {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(RetrofitService::class.java)
    }
}