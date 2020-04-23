package com.nicoqueijo.android.currencyconverter.kotlin.data

import android.content.Context
import android.net.ConnectivityManager
import com.nicoqueijo.android.currencyconverter.BuildConfig
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.model.ApiEndPoint
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class Repository(private val context: Context) {

    private val retrofitService = RetrofitFactory.getRetrofitService()
    private val currencyDao = CurrencyDatabase.getInstance(context).currencyDao()
    private val sharedPrefsProperties = context
            .getSharedPreferences(context.packageName.plus(".properties"), Context.MODE_PRIVATE)

    /**
     * Makes an API call if internet is available and the local data is either stale (hasn't been
     * updated in 24 hours) or we have no local data. If the call succeeds we store the data that
     * was returned into our local database. Else we throw an exception to let the called handle it.
     */
    suspend fun initCurrencies() {
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
                currencyDao.upsertCurrencies(retrofitResponse.body()?.exchangeRates?.currencies!!)
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

    val timestamp: Long
        get() = sharedPrefsProperties.getLong("timestamp", NO_DATA) * 1000L

    private val timeSinceLastUpdate: Long
        get() {
            return if (timestamp != NO_DATA) {
                System.currentTimeMillis() - timestamp
            } else {
                NO_DATA
            }
        }

    var isFirstLaunch: Boolean
        get() = sharedPrefsProperties.getBoolean("first_launch", true)
        set(value) = sharedPrefsProperties.edit().putBoolean("first_launch", value).apply()

    suspend fun getCurrency(currencyCode: String) = currencyDao.getCurrency(currencyCode)

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

    fun getAllCurrencies() = currencyDao.getAllCurrencies()

    fun getActiveCurrencies() = currencyDao.getActiveCurrencies()

    private fun getApiKey(): String {
        with(context.resources) {
            return when (BuildConfig.BUILD_TYPE) {
                "release" -> getString(R.string.openexchangerates_release_api_key)
                "debug" -> getString(R.string.openexchangerates_debug_api_key)
                else -> getString(R.string.openexchangerates_release_api_key)
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected)
    }

    private fun isDataStale() = timeSinceLastUpdate > TWENTY_FOUR_HOURS

    private fun isDataEmpty() = timeSinceLastUpdate == NO_DATA

    companion object {
        const val TWENTY_FOUR_HOURS = 86400000L
        const val NO_DATA = 0L
    }
}