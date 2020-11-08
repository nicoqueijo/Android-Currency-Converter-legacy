package com.nicoqueijo.android.currencyconverter.kotlin.util

import com.google.common.truth.Truth.assertThat
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.EMPTY
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.deepEquals
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.elementAfter
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.elementBefore
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.hasOnlyOneElement
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isNotLastElement
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isValid
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.roundToFourDecimalPlaces
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.toMillis
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.toSeconds
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UtilsTest {

    @Nested
    inner class ListHasOnlyOneElement {
        @Test
        fun emptyListShouldReturnFalse() {
            val list = listOf<Any>()
            assertThat(list.hasOnlyOneElement()).isFalse()
        }

        @Test
        fun listOfSizeOneShouldReturnTrue() {
            val list = listOf(Any())
            assertThat(list.hasOnlyOneElement()).isTrue()
        }

        @Test
        fun listOfSizeTwoShouldReturnFalse() {
            val list = listOf(Any(), Any())
            assertThat(list.hasOnlyOneElement()).isFalse()
        }

        @Test
        fun listOfSizeTenShouldReturnFalse() {
            val list = listOf(
                    Any(),
                    Any(),
                    Any(),
                    Any(),
                    Any(),
                    Any(),
                    Any(),
                    Any(),
                    Any(),
                    Any())
            assertThat(list.hasOnlyOneElement()).isFalse()
        }
    }

    @Nested
    inner class ListIsNotLastElement {
        @Test
        fun lastPositionShouldReturnFalse() {
            val list = listOf(Any(), Any(), Any())
            assertThat(list.isNotLastElement(2)).isFalse()
        }

        @ParameterizedTest
        @ValueSource(ints = [0, 1, 2, 3])
        fun nonLastPositionShouldReturnTrue(position: Int) {
            val list = listOf(Any(), Any(), Any(), Any(), Any())
            assertThat(list.isNotLastElement(position)).isTrue()
        }

        @ParameterizedTest
        @ValueSource(ints = [-3, -2, -1, 0, 1, 2, 3])
        fun positionForEmptyListShouldThrowException(position: Int) {
            val list = listOf<Any>()
            assertThrows(IllegalArgumentException::class.java) {
                list.isNotLastElement(position)
            }
        }

        @ParameterizedTest
        @ValueSource(ints = [-1, 3, 4, 5, 10])
        fun positionOutOfBoundShouldThrowException(position: Int) {
            val list = listOf(Any(), Any(), Any())
            assertThrows(IllegalArgumentException::class.java) {
                list.isNotLastElement(position)
            }
        }
    }

    @Nested
    inner class ListElementBefore {
        @ParameterizedTest
        @ValueSource(ints = [1, 2, 3])
        fun validPositionShouldReturnCorrectElement(position: Int) {
            val list = listOf(1, 2, 3, 4)
            val expected = list[position - 1]
            val actual = list.elementBefore(position)
            assertThat(actual).isEqualTo(expected)
        }

        @ParameterizedTest
        @ValueSource(ints = [0, 4, 7])
        fun invalidPositionShouldThrowException(position: Int) {
            val list = listOf(1, 2, 3, 4)
            assertThrows(IllegalArgumentException::class.java) {
                list.elementBefore(position)
            }
        }
    }

    @Nested
    inner class ListElementAfter {
        @ParameterizedTest
        @ValueSource(ints = [0, 1, 2])
        fun validPositionShouldReturnCorrectElement(position: Int) {
            val list = listOf(1, 2, 3, 4)
            val expected = list[position + 1]
            val actual = list.elementAfter(position)
            assertThat(actual).isEqualTo(expected)
        }

        @ParameterizedTest
        @ValueSource(ints = [-1, 3, 7])
        fun invalidPositionShouldThrowException(position: Int) {
            val list = listOf(1, 2, 3, 4)
            assertThrows(IllegalArgumentException::class.java) {
                list.elementAfter(position)
            }
        }
    }

    @Nested
    inner class ListCurrencyDeepEquals {
        @Test
        fun equalListsShouldReturnTrue() {
            val listA = listOf(
                    Currency("USD_BTC", 0.000076918096).apply {
                        order = 0
                        isSelected = true
                    },
                    Currency("USD_USD", 1.0),
                    Currency("USD_EUR", 0.842993).apply {
                        order = 2
                        isSelected = true
                    },
                    Currency("USD_XAU", 0.00052584).apply {
                        order = 3
                        isSelected = true
                    },
                    Currency("USD_GBP", 0.766548),
                    Currency("USD_ARS", 78.120127).apply {
                        order = 1
                        isSelected = true
                    },
                    Currency("USD_JPY", 104.70502716)
            )
            val listB = listOf(
                    Currency("USD_BTC", 0.000076918096).apply {
                        order = 0
                        isSelected = true
                    },
                    Currency("USD_USD", 1.0),
                    Currency("USD_EUR", 0.842993).apply {
                        order = 2
                        isSelected = true
                    },
                    Currency("USD_XAU", 0.00052584).apply {
                        order = 3
                        isSelected = true
                    },
                    Currency("USD_GBP", 0.766548),
                    Currency("USD_ARS", 78.120127).apply {
                        order = 1
                        isSelected = true
                    },
                    Currency("USD_JPY", 104.70502716)
            )
            val areListsTheSame = listA.deepEquals(listB)
            assertThat(areListsTheSame).isTrue()
        }

        @Test
        fun differentListsShouldReturnFalse() {
            val listA = listOf(
                    Currency("USD_BTC", 0.000076918096).apply {
                        order = 0
                        isSelected = true
                    },
                    Currency("USD_USD", 1.0),
                    Currency("USD_EUR", 0.842993).apply {
                        order = 2
                        isSelected = true
                    },
                    Currency("USD_XAU", 0.00052584).apply {
                        order = 3
                        isSelected = true
                    },
                    Currency("USD_GBP", 0.766548),
                    Currency("USD_ARS", 78.120127).apply {
                        order = 1
                        isSelected = true
                    },
                    Currency("USD_JPY", 104.70502716)
            )
            val listB = listOf(
                    Currency("USD_BTC", 0.000076918096).apply {
                        order = 0
                        isSelected = true
                    },
                    Currency("USD_SGD", 1.3581),
                    Currency("USD_EUR", 0.842993).apply {
                        order = 2
                        isSelected = true
                    },
                    Currency("USD_XAU", 0.00052584).apply {
                        order = 1
                        isSelected = true
                    },
                    Currency("USD_GBP", 0.766548),
                    Currency("USD_ARS", 78.120127),
                    Currency("USD_CNY", 6.6868)
            )
            val areListsTheSame = listA.deepEquals(listB)
            assertThat(areListsTheSame).isFalse()
        }

        @Test
        fun subsetListsWithDifferentSizeShouldReturnFalse() {
            val listA = listOf(
                    Currency("USD_BTC", 0.000076918096).apply {
                        order = 0
                        isSelected = true
                    },
                    Currency("USD_USD", 1.0),
                    Currency("USD_EUR", 0.842993).apply {
                        order = 2
                        isSelected = true
                    },
                    Currency("USD_XAU", 0.00052584).apply {
                        order = 3
                        isSelected = true
                    },
                    Currency("USD_GBP", 0.766548),
            )
            val listB = listOf(
                    Currency("USD_BTC", 0.000076918096).apply {
                        order = 0
                        isSelected = true
                    },
                    Currency("USD_USD", 1.0),
                    Currency("USD_EUR", 0.842993).apply {
                        order = 2
                        isSelected = true
                    },
                    Currency("USD_XAU", 0.00052584).apply {
                        order = 3
                        isSelected = true
                    },
                    Currency("USD_GBP", 0.766548),
                    Currency("USD_ARS", 78.120127).apply {
                        order = 1
                        isSelected = true
                    },
                    Currency("USD_JPY", 104.70502716)
            )
            val areListsTheSame = listA.deepEquals(listB)
            assertThat(areListsTheSame).isFalse()
        }

        @Test
        fun sameListsWithDifferentOrderShouldReturnFalse() {
            val listA = listOf(
                    Currency("USD_BTC", 0.000076918096).apply {
                        order = 0
                        isSelected = true
                    },
                    Currency("USD_USD", 1.0),
                    Currency("USD_EUR", 0.842993).apply {
                        order = 2
                        isSelected = true
                    },
                    Currency("USD_XAU", 0.00052584).apply {
                        order = 3
                        isSelected = true
                    },
                    Currency("USD_GBP", 0.766548),
                    Currency("USD_ARS", 78.120127).apply {
                        order = 1
                        isSelected = true
                    },
                    Currency("USD_JPY", 104.70502716)
            )
            val listB = listOf(
                    Currency("USD_ARS", 78.120127).apply {
                        order = 1
                        isSelected = true
                    },
                    Currency("USD_JPY", 104.70502716),
                    Currency("USD_EUR", 0.842993).apply {
                        order = 2
                        isSelected = true
                    },
                    Currency("USD_BTC", 0.000076918096).apply {
                        order = 0
                        isSelected = true
                    },
                    Currency("USD_GBP", 0.766548),
                    Currency("USD_USD", 1.0),
                    Currency("USD_XAU", 0.00052584).apply {
                        order = 3
                        isSelected = true
                    }
            )
            val areListsTheSame = listA.deepEquals(listB)
            assertThat(areListsTheSame).isFalse()
        }
    }

    @Nested
    inner class BigDecimalRoundToFourDecimalPlaces {
        @Test
        fun testDecimalZero() {
            val value = BigDecimal("0.00000000000")
            val expected = BigDecimal("0.0000")
            val actual = value.roundToFourDecimalPlaces()
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun testIntegerZero() {
            val value = BigDecimal("0")
            val expected = BigDecimal("0.0000")
            val actual = value.roundToFourDecimalPlaces()
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun testDecimalWithFourDecimalPlaces() {
            val value = BigDecimal("48762.2476")
            val expected = BigDecimal("48762.2476")
            val actual = value.roundToFourDecimalPlaces()
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun testDecimalWithLessThanFourDecimalPlaces() {
            val value = BigDecimal("48762.24")
            val expected = BigDecimal("48762.2400")
            val actual = value.roundToFourDecimalPlaces()
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun testDecimalWithMoreThanFourDecimalPlacesRoundedUp() {
            val value = BigDecimal("48762.247686241")
            val expected = BigDecimal("48762.2477")
            val actual = value.roundToFourDecimalPlaces()
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun testDecimalWithMoreThanFourDecimalPlacesRoundedDown() {
            val value = BigDecimal("48762.247635112")
            val expected = BigDecimal("48762.2476")
            val actual = value.roundToFourDecimalPlaces()
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun testDecimalWithMoreThanFourDecimalPlacesEndingWithFives() {
            val value = BigDecimal("48762.55555555555555")
            val expected = BigDecimal("48762.5556")
            val actual = value.roundToFourDecimalPlaces()
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Nested
    inner class IntIsValid {
        @Test
        fun minusOneShouldReturnFalse() {
            val value = -1
            assertThat(value.isValid()).isFalse()
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 2, 3, 4, 5, 10, 25, 500, 9999])
        fun nonMinusOneShouldReturnTrue(value: Int) {
            assertThat(value.isValid()).isTrue()
        }
    }

    @Nested
    inner class StringEmpty {
        @Test
        fun shouldReturnEmptyString() {
            val expected = ""
            val actual = String.EMPTY
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Nested
    inner class LongToSeconds {
        @Test
        fun zeroMillisShouldReturnZeroSeconds() {
            val timeInMillis = 0L
            val actual = timeInMillis.toSeconds()
            val expected = 0L
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun under1000MillisShouldReturnZeroSeconds() {
            val timeInMillis = 345L
            val actual = timeInMillis.toSeconds()
            val expected = 0L
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun millionsOfMillisShouldReturnCorrectSeconds() {
            val timeInMillis = 3_600_000L
            val actual = timeInMillis.toSeconds()
            val expected = 3_600L
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun trillionsOfMillisShouldReturnCorrectSeconds() {
            val timeInMillis = 1_604_779_208_123L
            val actual = timeInMillis.toSeconds()
            val expected = 1_604_779_208L
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun trillionsOfMillisShouldTruncateCorrectlyAndReturnCorrectSeconds() {
            val timeInMillis = 1_604_779_208_987L
            val actual = timeInMillis.toSeconds()
            val expected = 1_604_779_208L
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Nested
    inner class LongToMillis {
        @Test
        fun zeroSecondsShouldReturnZeroMillis() {
            val timeInSeconds = 0L
            val actual = timeInSeconds.toMillis()
            val expected = 0L
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun thousandsOfSecondsShouldReturnCorrectMillis() {
            val timeInSeconds = 3_600L
            val actual = timeInSeconds.toMillis()
            val expected = 3_600_000L
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun millionsOfSecondsShouldReturnCorrectMillis() {
            val timeInSeconds = 1_604_779_208L
            val actual = timeInSeconds.toMillis()
            val expected = 1_604_779_208_000L
            assertThat(actual).isEqualTo(expected)
        }
    }
}
