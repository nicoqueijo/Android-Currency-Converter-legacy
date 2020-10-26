package com.nicoqueijo.android.currencyconverter.kotlin.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.nicoqueijo.android.currencyconverter.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UtilsAndroidTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Nested
    inner class GetStringByResourceName {
        @Test
        fun unitedStatesDollarReturnsCorrectString() {
            val expected = "United States Dollar"
            val actual = Utils.getStringResourceByName("USD_USD", context)
            assertEquals(expected, actual)
        }

        @Test
        fun britishPoundReturnsCorrectString() {
            val expected = "British Pound"
            val actual = Utils.getStringResourceByName("USD_GBP", context)
            assertEquals(expected, actual)
        }

        @Test
        fun euroReturnsCorrectString() {
            val expected = "Euro"
            val actual = Utils.getStringResourceByName("USD_EUR", context)
            assertEquals(expected, actual)
        }

        @Test
        fun bitcoinReturnsCorrectString() {
            val expected = "Bitcoin"
            val actual = Utils.getStringResourceByName("USD_BTC", context)
            assertEquals(expected, actual)
        }
    }

    @Nested
    inner class GetDrawableResourceByName {
        @Test
        fun unitedStatesDollarReturnsCorrectDrawable() {
            val expected = R.drawable.usd_usd
            val actual = Utils.getDrawableResourceByName("usd_usd", context)
            assertEquals(expected, actual)
        }

        @Test
        fun britishPoundReturnsCorrectDrawable() {
            val expected = R.drawable.usd_gbp
            val actual = Utils.getDrawableResourceByName("usd_gbp", context)
            assertEquals(expected, actual)
        }

        @Test
        fun euroReturnsCorrectDrawable() {
            val expected = R.drawable.usd_eur
            val actual = Utils.getDrawableResourceByName("usd_eur", context)
            assertEquals(expected, actual)
        }

        @Test
        fun bitcoinReturnsCorrectDrawable() {
            val expected = R.drawable.usd_btc
            val actual = Utils.getDrawableResourceByName("usd_btc", context)
            assertEquals(expected, actual)
        }
    }
}