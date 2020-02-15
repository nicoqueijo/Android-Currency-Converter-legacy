package com.nicoqueijo.android.currencyconverter.kotlin.services

import com.nicoqueijo.android.currencyconverter.kotlin.models.ApiEndPoint
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface ExchangeRatesService {

    @GET
    fun getExchangeRates(@Url url: String): Call<ApiEndPoint>

}