package com.nicoqueijo.android.currencyconverter.kotlin.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ApiEndPoint {

    @Json(name = "timestamp")
    internal var timestamp: Long = 0L

    @Json(name = "rates")
    internal var exchangeRates: ExchangeRates? = null

}