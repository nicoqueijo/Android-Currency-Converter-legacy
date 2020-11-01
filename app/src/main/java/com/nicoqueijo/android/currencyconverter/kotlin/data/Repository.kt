package com.nicoqueijo.android.currencyconverter.kotlin.data

import androidx.lifecycle.LiveData
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.model.Resource

interface Repository {

    var isFirstLaunch: Boolean
    val timestamp: Long
    fun getAllCurrencies(): LiveData<MutableList<Currency>>
    fun getSelectedCurrencies(): LiveData<MutableList<Currency>>
    fun upsertCurrency(currency: Currency)
    fun upsertCurrencies(currencies: List<Currency>)
    suspend fun getCurrency(currencyCode: String): Currency
    suspend fun fetchCurrencies(): Resource

    companion object {
        const val TWENTY_FOUR_HOURS = 86400000L
        const val NO_DATA = 0L
        const val RELEASE = "release"
        const val DEBUG = "debug"
    }
}