package com.nicoqueijo.android.currencyconverter.kotlin.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CurrencyConversionTest {

    /**
     * Pool of a few selected currencies for testing.
     * Currency to Exchange Rate.
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

    /**
     * Most traded currency pairings. Amount value of 100.
     */
    @Test
    fun testCurrencyConversionFrom_EUR_To_USD() {
        val amount = BigDecimal("100")
        val from = exchangeRates["EUR"]
        val to = exchangeRates["USD"]
        val expected = BigDecimal("118.62494706361737286074736089149020217249728052308856")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_USD_To_JPY() {
        val amount = BigDecimal("100")
        val from = exchangeRates["USD"]
        val to = exchangeRates["JPY"]
        val expected = BigDecimal("10470.502716")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_GBP_To_USD() {
        val amount = BigDecimal("100")
        val from = exchangeRates["GBP"]
        val to = exchangeRates["USD"]
        val expected = BigDecimal("130.45497477000787948047610847592062075695194560549371")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_AUD_To_USD() {
        val amount = BigDecimal("100")
        val from = exchangeRates["AUD"]
        val to = exchangeRates["USD"]
        val expected = BigDecimal("71.38996985201573149375659018659196420221351740523159")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_USD_To_CAD() {
        val amount = BigDecimal("100")
        val from = exchangeRates["USD"]
        val to = exchangeRates["CAD"]
        val expected = BigDecimal("131.2575")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_USD_To_CNY() {
        val amount = BigDecimal("100")
        val from = exchangeRates["USD"]
        val to = exchangeRates["CNY"]
        val expected = BigDecimal("668.68")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_USD_To_CHF() {
        val amount = BigDecimal("100")
        val from = exchangeRates["USD"]
        val to = exchangeRates["CHF"]
        val expected = BigDecimal("90.4143")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_USD_To_HKD() {
        val amount = BigDecimal("100")
        val from = exchangeRates["USD"]
        val to = exchangeRates["HKD"]
        val expected = BigDecimal("775.0215")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_EUR_To_GBP() {
        val amount = BigDecimal("100")
        val from = exchangeRates["EUR"]
        val to = exchangeRates["GBP"]
        val expected = BigDecimal("90.93171592172176993166016799665003149492344539041248")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_USD_To_KRW() {
        val amount = BigDecimal("100")
        val from = exchangeRates["USD"]
        val to = exchangeRates["KRW"]
        val expected = BigDecimal("112844.5")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    /**
     * Currencies with large exchange rate difference.
     */
    @Test
    fun testCurrencyConversionFrom_BTC_To_VEF() {
        val amount = BigDecimal("99999999999999999999.9999")
        val from = exchangeRates["BTC"]
        val to = exchangeRates["VEF"]
        val expected = BigDecimal("323054853361164842145858305583.06820153218561208275358245997144807120550669676005")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_VEF_To_BTC() {
        val amount = BigDecimal("0.0001")
        val from = exchangeRates["VEF"]
        val to = exchangeRates["BTC"]
        val expected = BigDecimal("0.00000000000003095449548569488452849310239997030645")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    /**
     * Random currency pairings and random amount values.
     */
    @Test
    fun testCurrencyConversionFrom_CNY_To_AED() {
        val amount = BigDecimal("0")
        val from = exchangeRates["CNY"]
        val to = exchangeRates["AED"]
        val expected = BigDecimal("0")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_SGD_To_JPY() {
        val amount = BigDecimal("0.32")
        val from = exchangeRates["SGD"]
        val to = exchangeRates["JPY"]
        val expected = BigDecimal("24.67094373845814004859730505853766291142036668875613")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_INR_To_VEF() {
        val amount = BigDecimal("1")
        val from = exchangeRates["INR"]
        val to = exchangeRates["VEF"]
        val expected = BigDecimal("3365.48388843103089830383365962769260565740920698580059")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_RUB_To_XAG() {
        val amount = BigDecimal("13.99")
        val from = exchangeRates["RUB"]
        val to = exchangeRates["XAG"]
        val expected = BigDecimal("0.00746307403830888787716174674253745540710573583687")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_HKD_To_MXN() {
        val amount = BigDecimal("2567")
        val from = exchangeRates["HKD"]
        val to = exchangeRates["MXN"]
        val expected = BigDecimal("6910.32134592395178714396955439300716173680343061450534")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_COP_To_BTC() {
        val amount = BigDecimal("833984.1797")
        val from = exchangeRates["COP"]
        val to = exchangeRates["BTC"]
        val expected = BigDecimal("0.01697670925844740944604338074377907239097185490653")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_CAD_To_AUD() {
        val amount = BigDecimal("24612696.7")
        val from = exchangeRates["CAD"]
        val to = exchangeRates["AUD"]
        val expected = BigDecimal("26266237.88461756471058796640191989029198331523912919261755")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_XAU_To_USD() {
        val amount = BigDecimal("12345.6789")
        val from = exchangeRates["XAU"]
        val to = exchangeRates["USD"]
        val expected = BigDecimal("23478014.03468735737106344135098128708352350524874486535828")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_CHF_To_EUR() {
        val amount = BigDecimal("4300")
        val from = exchangeRates["CHF"]
        val to = exchangeRates["EUR"]
        val expected = BigDecimal("4009.17764114747335321956814353481694820398985558700338")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_BRL_To_KRW() {
        val amount = BigDecimal("30156.3")
        val from = exchangeRates["BRL"]
        val to = exchangeRates["KRW"]
        val expected = BigDecimal("6055918.47623464408334847473444414566994782756634291651328")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }

    @Test
    fun testCurrencyConversionFrom_GBP_To_ARS() {
        val amount = BigDecimal("49922182058874209463.4158")
        val from = exchangeRates["GBP"]
        val to = exchangeRates["ARS"]
        val expected = BigDecimal("5087649048143592730405.19856526479750778816199376947040498442367601246046")
        val actual = CurrencyConversion.convertCurrency(amount, from!!, to!!)
        assertEquals(expected.toDouble(), actual.toDouble())
    }
}