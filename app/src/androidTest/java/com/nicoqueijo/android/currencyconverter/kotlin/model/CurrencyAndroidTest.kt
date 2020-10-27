package com.nicoqueijo.android.currencyconverter.kotlin.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.util.*

/**
 * Decimal and Thousands Separators:
 * https://docs.oracle.com/cd/E19455-01/806-0169/overview-9/index.html
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class CurrencyAndroidTest {

    @Test
    fun conversionOnLocalesUsingCommaAsGroupingSeparatorShouldDisplayCorrectly() {
        Locale.setDefault(Locale("en", "US"))
        val currency = Currency("USD_EUR", 0.842993).apply {
            conversion.conversionValue = BigDecimal("123456789.1234")
        }
        val expected = "123,456,789.1234"
        val actual = currency.conversion.conversionText
        assertEquals(expected, actual)
    }

    @Test
    fun conversionOnLocalesUsingPeriodAsGroupingSeparatorShouldDisplayCorrectly() {
        Locale.setDefault(Locale("es", "ES"))
        val currency = Currency("USD_EUR", 0.842993).apply {
            conversion.conversionValue = BigDecimal("123456789.1234")
        }
        val expected = "123.456.789,1234"
        val actual = currency.conversion.conversionText
        assertEquals(expected, actual)
    }

    @Test
    fun conversionOnLocalesUsingSpaceAsGroupingSeparatorShouldDisplayCorrectly() {
        Locale.setDefault(Locale("fr", "FR"))
        val currency = Currency("USD_EUR", 0.842993).apply {
            conversion.conversionValue = BigDecimal("123456789.1234")
        }
        val expected = "123 456 789,1234" // Unicode character U+202F (Not a regular space)
        val actual = currency.conversion.conversionText
        assertEquals(expected, actual)
    }
}