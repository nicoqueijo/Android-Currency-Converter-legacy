package com.nicoqueijo.android.currencyconverter.kotlin.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.nicoqueijo.android.currencyconverter.R
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class UtilsAndroidTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Nested
    inner class GetStringByResourceName {
        @Test
        fun unitedStatesDollarReturnsCorrectStringInEnglishLocale() {
            setLocale("en", "US")
            val expected = "United States Dollar"
            val actual = Utils.getStringResourceByName("USD_USD", context)
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun britishPoundReturnsCorrectStringInEnglishLocale() {
            setLocale("en", "US")
            val expected = "British Pound"
            val actual = Utils.getStringResourceByName("USD_GBP", context)
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun unitedStatesDollarReturnsCorrectStringInSpanishLocale() {
            setLocale("es", "AR")
            val actual = Utils.getStringResourceByName("USD_USD", context)
            val expected = "Dólar Estadounidense"
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun britishPoundReturnsCorrectStringInSpanishLocale() {
            setLocale("es", "AR")
            val expected = "Libra Británica"
            val actual = Utils.getStringResourceByName("USD_GBP", context)
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun unitedStatesDollarReturnsCorrectStringInOtherLocales() {
            setLocale("fr", "FR")
            val actual = Utils.getStringResourceByName("USD_USD", context)
            val expected = "United States Dollar"
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun britishPoundReturnsCorrectStringInOtherLocales() {
            setLocale("fr", "FR")
            val expected = "British Pound"
            val actual = Utils.getStringResourceByName("USD_GBP", context)
            assertThat(actual).isEqualTo(expected)
        }

        /**
         * Credit: https://stackoverflow.com/a/21810126/5906793
         */
        private fun setLocale(language: String, country: String) {
            val locale = Locale(language, country)
            Locale.setDefault(locale)
            val res: Resources = context.resources
            val config: Configuration = res.configuration
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
        }
    }

    @Nested
    inner class GetDrawableResourceByName {
        @Test
        fun unitedStatesDollarReturnsCorrectDrawable() {
            val expected = R.drawable.usd_usd
            val actual = Utils.getDrawableResourceByName("usd_usd", context)
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun britishPoundReturnsCorrectDrawable() {
            val expected = R.drawable.usd_gbp
            val actual = Utils.getDrawableResourceByName("usd_gbp", context)
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun euroReturnsCorrectDrawable() {
            val expected = R.drawable.usd_eur
            val actual = Utils.getDrawableResourceByName("usd_eur", context)
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun bitcoinReturnsCorrectDrawable() {
            val expected = R.drawable.usd_btc
            val actual = Utils.getDrawableResourceByName("usd_btc", context)
            assertThat(actual).isEqualTo(expected)
        }
    }
}