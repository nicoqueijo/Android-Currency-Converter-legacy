package com.nicoqueijo.android.currencyconverter.kotlin.data

import android.content.Context
import android.net.ConnectivityManager
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import java.io.IOException
import java.util.*

const val TWENTY_FOUR_HOURS = 86400000L
const val NO_DATA = -1L

class Repository(private val context: Context) {

    private val sharedPrefsProperties = context
            .getSharedPreferences(context.packageName.plus(".properties"), Context.MODE_PRIVATE)

    private val currencyDao = CurrencyDatabase.getInstance(context).currencyDao()

    private val retrofitService = RetrofitFactory.getRetrofitService()

    private val timeSinceLastUpdate: Long
        get() {
            val lastUpdateTime = sharedPrefsProperties.getLong("timestamp", NO_DATA)
            return if (lastUpdateTime != NO_DATA) {
                System.currentTimeMillis() - lastUpdateTime
            } else {
                NO_DATA
            }
        }

    internal fun getAllCurrencies(): List<Currency> {
        if (isNetworkAvailable() &&
                (timeSinceLastUpdate > TWENTY_FOUR_HOURS ||
                        timeSinceLastUpdate == NO_DATA)) {
            val retrofitResponse = retrofitService.getExchangeRates(getApiKey())
            if (retrofitResponse.isSuccessful) {
                retrofitResponse.body()?.exchangeRates?.currencies?.forEach {
                    currencyDao.upsert(it)
                }
            } else {
                // We made a retrofit call but response wasn't in the 200s
                throw IOException(retrofitResponse.errorBody().toString())
            }
            return currencyDao.getAllCurrencies()
        } else if (timeSinceLastUpdate == NO_DATA) {
            return currencyDao.getAllCurrencies()
        } else {
            throw IOException("Network unavailable and no local data found.")
        }
    }

    internal fun getActiveCurrencies(): List<Currency> {
        return currencyDao.getActiveCurrencies()
    }


    private fun getApiKey(): String {
        val apiKeys = context.resources.getStringArray(R.array.api_keys)
        val random = Random().nextInt(apiKeys.size)
        return apiKeys[random]
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected)
    }
}