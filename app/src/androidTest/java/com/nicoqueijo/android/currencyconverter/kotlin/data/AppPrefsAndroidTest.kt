package com.nicoqueijo.android.currencyconverter.kotlin.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class AppPrefsAndroidTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val appPrefs = AppPrefs(context)

    @BeforeEach
    fun setUp() {
        clearSharedPrefs()
    }

    private fun clearSharedPrefs() = context
            .getSharedPreferences("${context.packageName}.properties", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

    @Nested
    inner class IsFirstLaunch {

        @Test
        fun whenNoValueExistsShouldReturnTrueByDefault() {
            val actual = appPrefs.isFirstLaunch
            assertThat(actual).isTrue()
        }

        @Test
        fun whenSetToFalseIsFirstLaunchShouldReturnFalse() {
            appPrefs.isFirstLaunch = false
            val actual = appPrefs.isFirstLaunch
            assertThat(actual).isFalse()
        }

        @Test
        fun whenSetToTrueIsFirstLaunchShouldReturnTrue() {
            appPrefs.isFirstLaunch = true
            val actual = appPrefs.isFirstLaunch
            assertThat(actual).isTrue()
        }
    }

    @Nested
    inner class Timestamp {

        @Test
        fun whenNoValueExistsShouldReturn0ByDefault() {
            val actual = appPrefs.timestamp
            assertThat(actual).isEqualTo(AppPrefs.NO_DATA)
        }

        @Test
        fun timestampShouldReturnCorrectValueWhenSetTo_1604779208() {
            appPrefs.timestamp = 1604779208L
            val actual = appPrefs.timestamp
            val expected = 1604779208L
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun timestampShouldReturnCorrectValueWhenSetTo_1748522368() {
            appPrefs.timestamp = 1748522368L
            val actual = appPrefs.timestamp
            val expected = 1748522368L
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Nested
    inner class IsDataStale {

        @Test
        fun whenDataHasBeenUpdatedInThePast24HoursShouldReturnFalse() {
            // Time units in seconds
            val oneHour = 3600L
            val currentTime = System.currentTimeMillis() / 1000L
            val twentyFourHours = AppPrefs.TWENTY_FOUR_HOURS / 1000L
            val timestamp = currentTime - twentyFourHours + oneHour
            appPrefs.timestamp = timestamp
            val actual = appPrefs.isDataStale
            assertThat(actual).isFalse()
        }

        @Test
        fun whenDataHasNotBeenUpdatedInThePast24HoursShouldReturnTrue() {
            // Time units in seconds
            val oneHour = 3600L
            val currentTime = System.currentTimeMillis() / 1000L
            val twentyFourHours = AppPrefs.TWENTY_FOUR_HOURS / 1000L
            val timestamp = currentTime - twentyFourHours - oneHour
            appPrefs.timestamp = timestamp
            val actual = appPrefs.isDataStale
            assertThat(actual).isTrue()
        }
    }

    @Nested
    inner class IsDataEmpty {

        @Test
        fun whenDataIsEmptyShouldReturnTrue() {
            val actual = appPrefs.isDataEmpty
            assertThat(actual).isTrue()
        }

        @Test
        fun whenDataIsNotEmptyShouldReturnFalse() {
            appPrefs.timestamp = 1604779208L
            val actual = appPrefs.isDataEmpty
            assertThat(actual).isFalse()
        }
    }

}