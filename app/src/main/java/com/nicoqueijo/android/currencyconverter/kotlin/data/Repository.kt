package com.nicoqueijo.android.currencyconverter.kotlin.data

import android.content.Context
import android.net.ConnectivityManager
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.model.ApiEndPoint
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*

const val TWENTY_FOUR_HOURS = 86400000L
const val NO_DATA = 0L

class Repository(private val context: Context) {

    private val retrofitService = RetrofitFactory.getRetrofitService()
    private val currencyDao = CurrencyDatabase.getInstance(context).currencyDao()
    private val sharedPrefsProperties = context
            .getSharedPreferences(context.packageName.plus(".properties"), Context.MODE_PRIVATE)

    private val timeSinceLastUpdate: Long
        get() {
            val lastUpdateTime = sharedPrefsProperties.getLong("timestamp", NO_DATA) * 1000L
            return if (lastUpdateTime != NO_DATA) {
                System.currentTimeMillis() - lastUpdateTime
            } else {
                NO_DATA
            }
        }

    internal suspend fun initCurrencies() {
        if (!isDataEmpty()) return // This will never update stale data?
        if (isNetworkAvailable() && (isDataStale() || isDataEmpty())) {
            val retrofitResponse: Response<ApiEndPoint>
            try {
                retrofitResponse = retrofitService.getExchangeRates(getApiKey())
            } catch (e: SocketTimeoutException) {
                throw SocketTimeoutException("Network request timed out.")
            }
            if (retrofitResponse.isSuccessful) {
                sharedPrefsProperties.edit()
                        .putLong("timestamp", retrofitResponse.body()!!.timestamp)
                        .apply()
                retrofitResponse.body()?.timestamp // Not sure if this line does anything
                retrofitResponse.body()?.exchangeRates?.currencies?.forEach {
                    currencyDao.upsert(it)
                }
            } else {
                // Retrofit call executed but response wasn't in the 200s
                throw IOException(retrofitResponse.errorBody()?.string())
            }
        } else {
            throw IOException("Network is unavailable and no local data found.")
        }
    }

    internal fun getAllCurrencies() = currencyDao.getAllCurrencies()

    internal fun getActiveCurrencies() = currencyDao.getActiveCurrencies()

    internal fun upsertCurrency(currency: Currency) {
        CoroutineScope(Dispatchers.IO).launch {
            currencyDao.upsert(currency)
        }
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

    private fun isDataStale() = timeSinceLastUpdate > TWENTY_FOUR_HOURS

    private fun isDataEmpty() = timeSinceLastUpdate == NO_DATA

}