package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.data.Currency
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MainActivity_kt : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val json: String = " { \"currencyCode\": \"USD_USD\", \"exchangeRate\": 1.0 }"
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<Currency> = moshi.adapter(Currency::class.java)
        val currency: Currency? = jsonAdapter.fromJson(json)
    }


}
