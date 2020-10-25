package com.nicoqueijo.android.currencyconverter.kotlin.util

import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.EMPTY
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.elementAfter
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.elementBefore
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.hasOnlyOneElement
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isNotLastElement
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isValid
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.roundToFourDecimalPlaces
import org.junit.jupiter.api.Assertions.assertEquals
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
            val expected = false
            val actual = list.hasOnlyOneElement()
            assertEquals(expected, actual)
        }

        @Test
        fun listOfSizeOneShouldReturnTrue() {
            val list = listOf(Any())
            val expected = true
            val actual = list.hasOnlyOneElement()
            assertEquals(expected, actual)
        }

        @Test
        fun listOfSizeTwoShouldReturnFalse() {
            val list = listOf(Any(), Any())
            val expected = false
            val actual = list.hasOnlyOneElement()
            assertEquals(expected, actual)
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
            val expected = false
            val actual = list.hasOnlyOneElement()
            assertEquals(expected, actual)
        }
    }

    @Nested
    inner class ListIsNotLastElement {
        @Test
        fun lastPositionShouldReturnFalse() {
            val list = listOf(Any(), Any(), Any())
            val expected = false
            val actual = list.isNotLastElement(2)
            assertEquals(expected, actual)
        }

        @ParameterizedTest
        @ValueSource(ints = [0, 1, 2, 3])
        fun nonLastPositionShouldReturnTrue(position: Int) {
            val list = listOf(Any(), Any(), Any(), Any(), Any())
            val expected = true
            val actual = list.isNotLastElement(position)
            assertEquals(expected, actual)
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
            assertEquals(expected, actual)
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
            assertEquals(expected, actual)
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
    inner class BigDecimalRoundToFourDecimalPlaces {
        @Test
        fun testDecimalZero() {
            val value = BigDecimal("0.00000000000")
            val expected = BigDecimal("0.0000")
            val actual = value.roundToFourDecimalPlaces()
            assertEquals(expected, actual)
        }

        @Test
        fun testIntegerZero() {
            val value = BigDecimal("0")
            val expected = BigDecimal("0.0000")
            val actual = value.roundToFourDecimalPlaces()
            assertEquals(expected, actual)
        }

        @Test
        fun testDecimalWithFourDecimalPlaces() {
            val value = BigDecimal("48762.2476")
            val expected = BigDecimal("48762.2476")
            val actual = value.roundToFourDecimalPlaces()
            assertEquals(expected, actual)
        }

        @Test
        fun testDecimalWithLessThanFourDecimalPlaces() {
            val value = BigDecimal("48762.24")
            val expected = BigDecimal("48762.2400")
            val actual = value.roundToFourDecimalPlaces()
            assertEquals(expected, actual)
        }

        @Test
        fun testDecimalWithMoreThanFourDecimalPlacesRoundedUp() {
            val value = BigDecimal("48762.247686241")
            val expected = BigDecimal("48762.2477")
            val actual = value.roundToFourDecimalPlaces()
            assertEquals(expected, actual)
        }

        @Test
        fun testDecimalWithMoreThanFourDecimalPlacesRoundedDown() {
            val value = BigDecimal("48762.247635112")
            val expected = BigDecimal("48762.2476")
            val actual = value.roundToFourDecimalPlaces()
            assertEquals(expected, actual)
        }

        @Test
        fun testDecimalWithMoreThanFourDecimalPlacesEndingWithFives() {
            val value = BigDecimal("48762.55555555555555")
            val expected = BigDecimal("48762.5556")
            val actual = value.roundToFourDecimalPlaces()
            assertEquals(expected, actual)
        }
    }

    @Nested
    inner class IntIsValid {
        @Test
        fun minusOneShouldReturnFalse() {
            val value = -1
            val expected = false
            val actual = value.isValid()
            assertEquals(expected, actual)
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 2, 3, 4, 5, 6])
        fun nonMinusOneShouldReturnTrue(value: Int) {
            val expected = true
            val actual = value.isValid()
            assertEquals(expected, actual)
        }
    }

    @Nested
    inner class StringEmpty {
        @Test
        fun shouldReturnEmptyString() {
            val expected = ""
            val actual = String.EMPTY
            assertEquals(expected, actual)
        }
    }
}