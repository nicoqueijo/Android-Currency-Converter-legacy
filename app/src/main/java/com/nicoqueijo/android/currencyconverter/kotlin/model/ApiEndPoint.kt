package com.nicoqueijo.android.currencyconverter.kotlin.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ApiEndPoint {

    @Json(name = "timestamp")
    internal var timestamp = 0L

    @Json(name = "rates")
    internal var exchangeRates: ExchangeRates? = null

}