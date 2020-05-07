package com.nicoqueijo.android.currencyconverter.kotlin.data

import com.nicoqueijo.android.currencyconverter.kotlin.model.ApiEndPoint
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API endpoint: https://openexchangerates.org/api/latest.json?app_id={app_id}
 */
interface ExchangeRateService {

    @GET("api/latest.json")
    suspend fun getExchangeRates(@Query("app_id") app_id: String): Response<ApiEndPoint>

    companion object {
        const val BASE_URL = "https://openexchangerates.org/"
    }
}