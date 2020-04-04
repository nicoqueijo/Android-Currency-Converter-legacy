package com.nicoqueijo.android.currencyconverter.kotlin.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.Order.INVALID
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.roundToFourDecimalPlaces
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

@Entity(tableName = "table_currency")
data class Currency(@PrimaryKey
                    @ColumnInfo(name = "column_currencyCode")
                    val currencyCode: String,
                    @ColumnInfo(name = "column_exchangeRate")
                    val exchangeRate: Double) {

    @Ignore
    var conversionValue = BigDecimal.ZERO!!

    @Ignore
    var conversion = Conversion(BigDecimal.ZERO)

    @ColumnInfo(name = "column_isSelected")
    var isSelected = false

    @ColumnInfo(name = "column_order")
    var order = INVALID.position

    @Ignore
    var isFocused = false

    @Ignore
    private var decimalFormatter: DecimalFormat

    @Ignore
    private var decimalSeparator: String

    init {
        val numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault())
        val conversionPattern = "###,##0.####"
        decimalFormatter = numberFormatter as DecimalFormat
        decimalFormatter.applyPattern(conversionPattern)
        decimalSeparator = decimalFormatter.decimalFormatSymbols.decimalSeparator.toString()
    }

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

    /*override fun toString() = "ordr: $order " +
            "slct: ${isSelected.toString().capitalize().take(1)} " +
            "fcsd: ${isFocused.toString().capitalize().take(1)} " +
            currencyCode*/

    /* Structure is for debugging purposes.
       Example: 4 S* F* USD_EUR
                | |  |    |
            Order |  |    |
           Selected? |    |
                Focused?  |
                    Currency code

        *blank if not selected/focused      */
    override fun toString(): String {
        val string = StringBuilder()
        string.append("{")
        string.append(order)
        string.append(" ")
        string.append(trimmedCurrencyCode)
        string.append(" ")
        if (isFocused) {
            string.append("F")
        } else {
            string.append(" ")
        }
        string.append(" ")
        if (isSelected) {
            string.append("S")
        } else {
            string.append(" ")
        }
        string.append("}")
        return string.toString()
    }

    inner class Conversion(conversionValue: BigDecimal) {

        // The raw underlying conversion result
        var conversionValue: BigDecimal = conversionValue
            set(value) {
                field = value.roundToFourDecimalPlaces()
                // This field needs to be formatted not just converted to String.
                conversionText = decimalFormatter.format(conversionValue)
            }

        /**
         * The conversion result formatted.
         * Examples: 57,204.2719
         *           0.0631
         *           23.6000
         */
        var conversionText = ""

        // The hint displayed when it is empty
        var conversionHint = ""
    }

    companion object {
        const val CURRENCY_CODE_STARTING_INDEX = 4
    }
}