package com.nicoqueijo.android.currencyconverter.kotlin.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.database.CurrencyDatabase
import com.nicoqueijo.android.currencyconverter.kotlin.models.ApiEndPoint
import com.nicoqueijo.android.currencyconverter.kotlin.services.ExchangeRatesService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

class MainActivity_kt : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_kt)

        val currencyDatabase: CurrencyDatabase = CurrencyDatabase.getInstance(applicationContext)
        val dao = currencyDatabase.currencyDao()
        val currencies = dao.getAllCurrencies()


        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://openexchangerates.org/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
        val exchangeRatesService: ExchangeRatesService = retrofit
                .create(ExchangeRatesService::class.java)
        exchangeRatesService
                .getExchangeRates(getApiKey())
                .enqueue(object : Callback<ApiEndPoint> {
                    override fun onResponse(call: Call<ApiEndPoint>, response: Response<ApiEndPoint>) {

                        response.body()?.exchangeRates?.currencies
                        response.body()?.exchangeRates?.currencies

//                        view_code.append(response.code().toString())
//                        view_body.append(response.body()?.exchangeRates?.currencies.toString())
                    }

                    override fun onFailure(call: Call<ApiEndPoint>, t: Throwable) {


//                        view_code.text = t.message
                    }
                })
    }

    private fun getApiKey(): String {
        val apiKeys = resources.getStringArray(R.array.api_keys)
        val random = Random().nextInt(apiKeys.size)
        return apiKeys[random]
    }
}
