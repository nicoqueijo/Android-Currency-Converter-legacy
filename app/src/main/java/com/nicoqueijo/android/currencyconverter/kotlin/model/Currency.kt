package com.nicoqueijo.android.currencyconverter.kotlin.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "table_currency")
class Currency(@PrimaryKey
               @ColumnInfo(name = "column_currencyCode")
               val currencyCode: String,
               @ColumnInfo(name = "column_exchangeRate")
               val exchangeRate: Double) {

    @Ignore
    var conversionValue = BigDecimal(0.0)
    @ColumnInfo(name = "column_isSelected")
    var isSelected = false
    @ColumnInfo(name = "column_order")
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

    override fun toString() = "{$currencyCode : $exchangeRate : $isSelected : $order}"

    companion object {
        const val CURRENCY_CODE_STARTING_INDEX = 4
    }
}