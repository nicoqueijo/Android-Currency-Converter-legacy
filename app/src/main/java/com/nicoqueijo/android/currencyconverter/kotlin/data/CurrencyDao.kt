package com.nicoqueijo.android.currencyconverter.kotlin.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(currency: Currency?)

    @Query("SELECT * FROM table_currency ORDER BY column_currencyCode ASC")
    fun getAllCurrencies(): LiveData<MutableList<Currency>>

    @Query("SELECT * FROM table_currency WHERE column_isSelected = 1 ORDER BY column_order ASC")
    fun getActiveCurrencies(): LiveData<MutableList<Currency>>
}
