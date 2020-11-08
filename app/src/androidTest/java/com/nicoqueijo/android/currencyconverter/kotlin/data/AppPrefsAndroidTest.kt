package com.nicoqueijo.android.currencyconverter.kotlin.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.toSeconds
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
    inner class TimestampInSeconds {
        @Test
        fun whenNoValueExistsShouldReturn0ByDefault() {
            val actual = appPrefs.timestampInSeconds
            assertThat(actual).isEqualTo(AppPrefs.NO_DATA)
        }

        @Test
        fun timestampShouldReturnCorrectValueWhenSetTo_1604779208() {
            appPrefs.timestampInSeconds = 1_604_779_208L
            val actual = appPrefs.timestampInSeconds
            val expected = 1_604_779_208L
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun timestampShouldReturnCorrectValueWhenSetTo_1748522368() {
            appPrefs.timestampInSeconds = 1_748_522_368L
            val actual = appPrefs.timestampInSeconds
            val expected = 1_748_522_368L
            assertThat(actual).isEqualTo(expected)
        }
    }

    /**
     * Note: These tests depend on the system clock.
     */
    @Nested
    inner class IsDataStale {
        @Test
        fun whenDataHasBeenUpdatedInThePast24HoursShouldReturnFalse() {
            val oneHour = 3_600L
            val currentTime = System.currentTimeMillis().toSeconds()
            val twentyFourHours = AppPrefs.TWENTY_FOUR_HOURS_IN_MILLIS.toSeconds()
            val timestamp = currentTime - twentyFourHours + oneHour
            appPrefs.timestampInSeconds = timestamp
            val actual = appPrefs.isDataStale
            assertThat(actual).isFalse()
        }

        @Test
        fun whenDataHasNotBeenUpdatedInThePast24HoursShouldReturnTrue() {
            val oneHour = 3_600L
            val currentTime = System.currentTimeMillis().toSeconds()
            val twentyFourHours = AppPrefs.TWENTY_FOUR_HOURS_IN_MILLIS.toSeconds()
            val timestamp = currentTime - twentyFourHours - oneHour
            appPrefs.timestampInSeconds = timestamp
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
            appPrefs.timestampInSeconds = 1_604_779_208L
            val actual = appPrefs.isDataEmpty
            assertThat(actual).isFalse()
        }
    }
}