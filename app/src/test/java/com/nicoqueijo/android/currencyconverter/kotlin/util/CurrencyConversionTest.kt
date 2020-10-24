package com.nicoqueijo.android.currencyconverter.kotlin.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class CurrencyConversionTest {

    @Test
    fun testCurrencyConversionFrom_CNY_To_AED() {
        val amount = BigDecimal("")
        val from = exchangeRates[""]
        val to = exchangeRates[""]
        val expected = BigDecimal("")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected, actual)
    }

    @Test
    fun testCurrencyConversionFrom_SGD_To_JPY() {
        val amount = BigDecimal("")
        val from = exchangeRates[""]
        val to = exchangeRates[""]
        val expected = BigDecimal("")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected, actual)
    }

    @Test
    fun testCurrencyConversionFrom_INR_To_VEF() {
        val amount = BigDecimal("")
        val from = exchangeRates[""]
        val to = exchangeRates[""]
        val expected = BigDecimal("")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected, actual)
    }

    @Test
    fun testCurrencyConversionFrom_RUB_To_XAG() {
        val amount = BigDecimal("")
        val from = exchangeRates[""]
        val to = exchangeRates[""]
        val expected = BigDecimal("")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected, actual)
    }

    @Test
    fun testCurrencyConversionFrom_HKD_To_MXN() {
        val amount = BigDecimal("")
        val from = exchangeRates[""]
        val to = exchangeRates[""]
        val expected = BigDecimal("")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected, actual)
    }

    @Test
    fun testCurrencyConversionFrom_COP_To_BTC() {
        val amount = BigDecimal("")
        val from = exchangeRates[""]
        val to = exchangeRates[""]
        val expected = BigDecimal("")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected, actual)
    }

    @Test
    fun testCurrencyConversionFrom_CAD_To_AUD() {
        val amount = BigDecimal("")
        val from = exchangeRates[""]
        val to = exchangeRates[""]
        val expected = BigDecimal("")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected, actual)
    }

    @Test
    fun testCurrencyConversionFrom_XAU_To_USD() {
        val amount = BigDecimal("")
        val from = exchangeRates[""]
        val to = exchangeRates[""]
        val expected = BigDecimal("")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected, actual)
    }

    @Test
    fun testCurrencyConversionFrom_CHF_To_EUR() {
        val amount = BigDecimal("")
        val from = exchangeRates[""]
        val to = exchangeRates[""]
        val expected = BigDecimal("")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected, actual)
    }

    @Test
    fun testCurrencyConversionFrom_BRL_To_KRW() {
        val amount = BigDecimal("")
        val from = exchangeRates[""]
        val to = exchangeRates[""]
        val expected = BigDecimal("")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected, actual)
    }

    @Test
    fun testCurrencyConversionFrom_GBP_To_ARS() {
        val amount = BigDecimal("")
        val from = exchangeRates[""]
        val to = exchangeRates[""]
        val expected = BigDecimal("")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected, actual)
    }

    /**
     * Pool of a few selected exchange rates.
     */
    private val exchangeRates = mapOf(
            "AED" to 3.673, // Emirati Dirham
            "ARS" to 78.120127, // Argentine Peso
            "AUD" to 1.400757, // Australian Dollar
            "BRL" to 5.619251, // Brazilian Real
            "BTC" to 0.000076918096, // Bitcoin
            "CAD" to 1.312575, // Canadian Dollar
            "CHF" to 0.904143, // Swiss Franc
            "CNY" to 6.6868, // Chinese Yuan Renminbi
            "COP" to 3778.616587, // Colombian Peso
            "EUR" to 0.842993, // Euro
            "GBP" to 0.766548, // British Pound
            "HKD" to 7.750215, // Hong Kong Dollar
            "INR" to 73.83415, // Indian Rupee
            "JPY" to 104.70502716, // Japanese Yen
            "KRW" to 1128.445, // South Korean Won
            "MXN" to 20.86345, // Mexican Peso
            "RUB" to 76.2173, // Russian Ruble
            "SGD" to 1.3581, // Singapore Dollar
            "USD" to 1.0, // United States Dollar
            "VEF" to 248487.642241, // Venezuelan Bolívar
            "XAG" to 0.04065871, // Silver
            "XAU" to 0.00052584, // Gold
    )
}