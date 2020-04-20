package com.nicoqueijo.android.currencyconverter.kotlin.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.Order.INVALID
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.extractTrailingZeros
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isDecimaledZero
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

    @ColumnInfo(name = "column_order")
    var order = INVALID.position

    @ColumnInfo(name = "column_isSelected")
    var isSelected = false

    @Ignore
    var isFocused = false

    @Ignore
    var conversion = Conversion(BigDecimal.ZERO)

    @Ignore
    private var decimalFormatter: DecimalFormat

    @Ignore
    private var decimalSeparator: String

    @Ignore
    private var groupingSeparator: String

    init {
        val numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault())
        val conversionPattern = "#,##0.####"
        decimalFormatter = numberFormatter as DecimalFormat
        decimalFormatter.applyPattern(conversionPattern)
        decimalSeparator = decimalFormatter.decimalFormatSymbols.decimalSeparator.toString()
        groupingSeparator = decimalFormatter.decimalFormatSymbols.groupingSeparator.toString()
    }

    /**
     * Currency code without the "USD_" prefix. E.g. USD_EUR -> EUR
     */
    val trimmedCurrencyCode
        get() = currencyCode.substring(CURRENCY_CODE_START_INDEX)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Currency
        if (currencyCode != other.currencyCode) return false
        return true
    }

    override fun hashCode() = currencyCode.hashCode()

    /**
     * Since the toString() method is really only useful for debugging I've structured it in a way
     * which concisely displays the object's state.
     *
     * Example: 4 S* F* EUR
     *          | |  |   |
     *      Order |  |   |
     *     Selected? |   |
     *         Focused?  |
     *            Currency code
     *
     *    *blank if not selected/focused
     */
    override fun toString() = StringBuilder().apply {
        append("{")
        append(order)
        append(" ")
        append(trimmedCurrencyCode)
        append(" ")
        append(if (isFocused) "F" else " ")
        append(" ")
        append(if (isSelected) "S" else " ")
        append("}")
    }.toString()

    inner class Conversion(conversionValue: BigDecimal) {
        /**
         * The underlying numeric conversion result.
         * Example: 1234.5678
         */
        var conversionValue: BigDecimal = conversionValue
            set(value) {
                field = value.roundToFourDecimalPlaces()
                conversionString = field.toString()
            }

        /**
         * The [conversionValue] as a String.
         * Example: "1234.5678"
         */
        var conversionString: String = ""

        /**
         * The [conversionString] formatted according to locale.
         * Example:    USA: 1,234.5678
         *          France: 1.234,5678
         */
        val conversionText: String
            get() {
                return if (conversionString.isNotBlank()) {
                    when {
                        conversionString.endsWith(decimalSeparator.take(1).single()) -> {
                            try {
                                decimalFormatter.format(BigDecimal(conversionString.replace(",", "."))).plus(decimalSeparator.take(1).single())
                            } catch (e: NumberFormatException) {
                                e.printStackTrace()
                                conversionString
                            }
                        }
                        conversionString.isDecimaledZero() -> {
                            if (decimalSeparator == ",") {
                                return conversionString.replace(".", ",")
                            }
                            conversionString
                        }
                        conversionString.contains(decimalSeparator) && conversionString.endsWith('0') -> {
                            val trailingZeros = conversionString.extractTrailingZeros()
                            val formattedString = decimalFormatter.format(BigDecimal(conversionString.replace(",", ".")))
                            if (formattedString.contains(decimalSeparator)) {
                                decimalFormatter.format(BigDecimal(conversionString.replace(",", "."))).plus(trailingZeros)
                            } else {
                                decimalFormatter.format(BigDecimal(conversionString.replace(",", "."))).plus(decimalSeparator).plus(trailingZeros)
                            }
                        }
                        else -> {
                            try {
                                decimalFormatter.format(BigDecimal(conversionString.replace(",", ".")))
                            } catch (e: NumberFormatException) {
                                e.printStackTrace()
                                return conversionString
                            }
                        }
                    }
                } else {
                    ""
                }
            }

        /**
         * The hint displayed when [conversionText] is empty.
         */
        var conversionHint: String = ""
            set(value) {
                field = decimalFormatter.format(BigDecimal(value))
            }

        var hasInvalidInput = false

        override fun toString() = "value: $conversionValue string: $conversionString " +
                "text: $conversionString hint: $conversionHint"
    }

    companion object {
        const val CURRENCY_CODE_START_INDEX = 4
    }
}