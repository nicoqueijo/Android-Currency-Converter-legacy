package com.nicoqueijo.android.currencyconverter.kotlin.data

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.nicoqueijo.android.currencyconverter.BuildConfig
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.model.ApiEndPoint
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.model.ExchangeRates
import com.nicoqueijo.android.currencyconverter.kotlin.model.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.net.SocketTimeoutException
import javax.inject.Inject

class Repository @Inject constructor(
        private val context: Context,
        private val exchangeRateService: ExchangeRateService,
        private val currencyDao: CurrencyDao,
        private val sharedPreferences: SharedPreferences
) {

    var isFirstLaunch: Boolean
        get() = sharedPreferences.getBoolean("first_launch", true)
        set(value) = sharedPreferences.edit().putBoolean("first_launch", value).apply()

    val timestamp: Long
        get() = sharedPreferences.getLong("timestamp", NO_DATA) * 1000L

    private val isDataStale
        get() = timeSinceLastUpdate > TWENTY_FOUR_HOURS

    private val isDataEmpty
        get() = timeSinceLastUpdate == NO_DATA

    private val timeSinceLastUpdate: Long
        get() {
            return if (timestamp != NO_DATA) {
                System.currentTimeMillis() - timestamp
            } else {
                NO_DATA
            }
        }

    fun getAllCurrencies() = currencyDao.getAllCurrencies()

    fun getSelectedCurrencies() = currencyDao.getSelectedCurrencies()

    fun upsertCurrency(currency: Currency) {
        CoroutineScope(Dispatchers.IO).launch {
            currencyDao.upsertCurrency(currency)
        }
    }

    fun upsertCurrencies(currencies: List<Currency>) {
        CoroutineScope(Dispatchers.IO).launch {
            currencyDao.upsertCurrencies(currencies)
        }
    }

    suspend fun getCurrency(currencyCode: String) = currencyDao.getCurrency(currencyCode)

    /**
     * Makes an API call and persists the response if it's successful.
     */
    suspend fun fetchCurrencies(): Resource {
        if (isNetworkAvailable() && (isDataStale || isDataEmpty)) {
            val retrofitResponse: Response<ApiEndPoint>
            try {
                retrofitResponse = exchangeRateService.getExchangeRates(getApiKey())
            } catch (e: SocketTimeoutException) {
                return Resource.Error("Network request timed out.")
            }
            return if (retrofitResponse.isSuccessful) {
                persistResponse(retrofitResponse)
                Resource.Success
            } else {
                // Retrofit call executed but response wasn't in the 200s
                Resource.Error(retrofitResponse.errorBody()?.string())
            }
        } else if (!isDataEmpty) {
            return Resource.Success
        } else {
            return Resource.Error("Network is unavailable and no local data found.")
        }
    }

    private suspend fun persistResponse(response: Response<ApiEndPoint>) {
        response.body()?.let { responseBody ->
            responseBody.exchangeRates?.let { exchangeRates ->
                persistCurrencies(exchangeRates)
            }
            persistTimestamp(responseBody.timestamp)
        }
    }

    private suspend fun persistCurrencies(exchangeRates: ExchangeRates) {
        when {
            isDataEmpty -> currencyDao.upsertCurrencies(exchangeRates.currencies)
            isDataStale -> currencyDao.updateExchangeRates(exchangeRates.currencies)
        }
    }

    private fun persistTimestamp(timestamp: Long) {
        sharedPreferences.edit()
                .putLong("timestamp", timestamp)
                .apply()
    }

    private fun getApiKey(): String {
        with(context.resources) {
            return when (BuildConfig.BUILD_TYPE) {
                RELEASE -> getString(R.string.openexchangerates_release_api_key)
                DEBUG -> getString(R.string.openexchangerates_debug_api_key)
                else -> getString(R.string.openexchangerates_release_api_key)
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected)
    }

    companion object {
        const val TWENTY_FOUR_HOURS = 86400000L
        const val NO_DATA = 0L
        const val RELEASE = "release"
        const val DEBUG = "debug"
    }
}