package com.nicoqueijo.android.currencyconverter.kotlin.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "currency")
class Currency(@PrimaryKey
               @ColumnInfo(name = "currency_code")
               val currencyCode: String,
               @ColumnInfo(name = "exchange_rate")
               val exchangeRate: Double) {

    @Ignore
    var conversionValue = BigDecimal(0.0)
    @ColumnInfo(name = "is_selected")
    var isSelected = false
    @ColumnInfo(name = "order")
    var order = -1

    val trimmedCurrencyCode
        get() = currencyCode.substring(CURRENCY_CODE_STARTING_INDEX)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Currency
        if (currencyCode != other.currencyCode) return false
        return true
    }

    override fun hashCode() = currencyCode.hashCode()

    override fun toString() = "{$currencyCode : $exchangeRate}"

    companion object {
        val TAG: String = Currency::class.java.simpleName
        const val CURRENCY_CODE_STARTING_INDEX = 4
    }

}