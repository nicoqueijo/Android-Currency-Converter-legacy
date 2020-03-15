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

class Repository(private val context: Context) {

    private val retrofitService = RetrofitFactory.getRetrofitService()
    private val currencyDao = CurrencyDatabase.getInstance(context).currencyDao()
    private val sharedPrefsProperties = context
            .getSharedPreferences(context.packageName.plus(".properties"), Context.MODE_PRIVATE)

    internal suspend fun initCurrencies() {
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
                retrofitResponse.body()?.exchangeRates?.currencies?.forEach {
                    currencyDao.upsert(it)
                }
            } else {
                // Retrofit call executed but response wasn't in the 200s
                throw IOException(retrofitResponse.errorBody()?.string())
            }
        } else if (!isDataEmpty()) {
            return
        } else {
            throw IOException("Network is unavailable and no local data found.")
        }
    }

    private val timeSinceLastUpdate: Long
        get() {
            val lastUpdateTime = sharedPrefsProperties.getLong("timestamp", NO_DATA) * 1000L
            return if (lastUpdateTime != NO_DATA) {
                System.currentTimeMillis() - lastUpdateTime
            } else {
                NO_DATA
            }
        }

    internal var isFirstLaunch: Boolean
        get() = sharedPrefsProperties.getBoolean("first_launch", true)
        set(value) {
            sharedPrefsProperties.edit().putBoolean("first_launch", value).apply()
        }

    internal suspend fun getCurrency(currencyCode: String) = currencyDao.getCurrency(currencyCode)

    internal fun upsertCurrency(currency: Currency?) {
        CoroutineScope(Dispatchers.IO).launch {
            currencyDao.upsert(currency)
        }
    }

    internal fun getAllCurrencies() = currencyDao.getAllCurrencies()

    internal fun getActiveCurrencies() = currencyDao.getActiveCurrencies()

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

    companion object {
        private const val TWENTY_FOUR_HOURS = 86400000L
        private const val NO_DATA = 0L
    }
}