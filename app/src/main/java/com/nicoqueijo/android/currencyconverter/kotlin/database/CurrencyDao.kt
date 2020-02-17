package com.nicoqueijo.android.currencyconverter.kotlin.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nicoqueijo.android.currencyconverter.kotlin.models.Currency

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(currency: Currency?)

    @Query("SELECT * FROM currency ORDER BY 'currency_code' ASC")
    fun getAllCurrencies(): List<Currency>

    @Query("SELECT * FROM currency WHERE is_selected = 'true' ORDER BY 'order' ASC")
    fun getActiveCurrencies(): List<Currency>
}
