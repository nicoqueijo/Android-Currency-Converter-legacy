package com.nicoqueijo.android.currencyconverter.kotlin.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCurrency(currency: Currency)

    @Transaction
    suspend fun upsertCurrencies(currencies: List<Currency>) {
        currencies.forEach {
            upsertCurrency(it)
        }
    }

    @Query("UPDATE table_currency SET column_exchangeRate = :exchangeRate WHERE column_currencyCode = :currencyCode")
    suspend fun updateExchangeRate(currencyCode: String, exchangeRate: Double)

    @Transaction
    suspend fun updateExchangeRates(currencies: List<Currency>) {
        currencies.forEach {
            updateExchangeRate(it.currencyCode, it.exchangeRate)
        }
    }

    @Query("SELECT * FROM table_currency WHERE column_currencyCode = :currencyCode")
    suspend fun getCurrency(currencyCode: String): Currency

    @Query("SELECT * FROM table_currency ORDER BY column_currencyCode ASC")
    fun getAllCurrencies(): LiveData<MutableList<Currency>>

    @Query("SELECT * FROM table_currency WHERE column_isSelected = 1 ORDER BY column_order ASC")
    fun getActiveCurrencies(): LiveData<MutableList<Currency>>
}
