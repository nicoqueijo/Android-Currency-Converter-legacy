package com.nicoqueijo.android.currencyconverter.kotlin.data

import java.math.BigDecimal

data class Currency(val currencyCode: String, val exchangeRate: Double = 0.0) {

    @Transient
    var conversionValue: BigDecimal = BigDecimal(0.0)
    @Transient
    var isSelected: Boolean = false

    val trimmedCurrencyCode: String
        get() = currencyCode.substring(CURRENCY_CODE_STARTING_INDEX)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Currency
        if (currencyCode != other.currencyCode) return false
        return true
    }

    override fun hashCode(): Int {
        return currencyCode.hashCode()
    }

    companion object {
        val TAG = Currency::class.java.simpleName
        const val CURRENCY_CODE_STARTING_INDEX = 4
    }

}