package com.nicoqueijo.android.currencyconverter.kotlin.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class CurrencyTest {

    @Nested
    inner class Equals {
        @Test
        fun sameCurrencyObjectShouldReturnTrue() {
            val currency = Currency("USD_EUR", 0.842993)
            assertEquals(currency, currency)
        }

        @Test
        fun sameCurrenciesShouldReturnTrue() {
            val currencyA = Currency("USD_EUR", 0.842993)
            val currencyB = currencyA.copy()
            assertFalse(currencyA === currencyB)
            val currencyC = Currency("USD_EUR", 0.842993)
            assertEquals(currencyA, currencyB)
            assertEquals(currencyB, currencyC)
        }

        @Test
        fun currenciesWithSameCurrencyCodeButDifferentExchangeRatesShouldReturnTrue() {
            val currencyA = Currency("USD_EUR", 0.842993)
            val currencyB = Currency("USD_EUR", 0.8641)
            assertEquals(currencyA, currencyB)
        }

        @Test
        fun differentTypeOfCurrencyObjectsShouldReturnFalse() {
            val currencyA = Currency("USD_EUR", 0.842993)
            val currencyB = java.util.Currency.getInstance("EUR")
            assertNotEquals(currencyA, currencyB)
        }

        @Test
        fun currenciesWithDifferentCurrencyCodesButSameExchangeRatesShouldReturnFalse() {
            val currencyA = Currency("USD_USD", 1.0)
            val currencyB = Currency("USD_BSD", 1.0)
            assertNotEquals(currencyA, currencyB)
        }
    }

    @Nested
    inner class DeepEquals {
        @Test
        fun sameCurrencyObjectShouldReturnTrue() {
            val currency = Currency("USD_EUR", 0.842993)
            val areEqual = currency.deepEquals(currency)
            assertTrue(areEqual)
        }

        @Test
        fun currenciesWithEqualPropertiesShouldReturnTrue() {
            val currencyA = Currency("USD_EUR", 0.842993).apply {
                order = 7
                isSelected = true
            }
            val currencyB = Currency("USD_EUR", 0.842993).apply {
                order = 7
                isSelected = true
            }
            val areEqual = currencyA.deepEquals(currencyB)
            assertTrue(areEqual)
        }

        @Test
        fun currenciesWithDifferentCurrencyCodePropertyShouldReturnFalse() {
            val currencyA = Currency("USD_USD", 1.0).apply {
                order = 7
                isSelected = true
            }
            val currencyB = Currency("USD_BSD", 1.0).apply {
                order = 7
                isSelected = true
            }
            val areEqual = currencyA.deepEquals(currencyB)
            assertFalse(areEqual)
        }

        @Test
        fun currenciesWithDifferentExchangeRatePropertyShouldReturnFalse() {
            val currencyA = Currency("USD_EUR", 0.842993).apply {
                order = 7
                isSelected = true
            }
            val currencyB = Currency("USD_EUR", 0.8641).apply {
                order = 7
                isSelected = true
            }
            val areEqual = currencyA.deepEquals(currencyB)
            assertFalse(areEqual)
        }

        @Test
        fun currenciesWithDifferentIsSelectedPropertyShouldReturnFalse() {
            val currencyA = Currency("USD_EUR", 0.842993).apply {
                order = 7
                isSelected = true
            }
            val currencyB = Currency("USD_EUR", 0.842993).apply {
                order = 7
                isSelected = false
            }
            val areEqual = currencyA.deepEquals(currencyB)
            assertFalse(areEqual)
        }

        @Test
        fun currenciesWithDifferentOrderPropertyShouldReturnFalse() {
            val currencyA = Currency("USD_EUR", 0.842993).apply {
                order = 1
                isSelected = true
            }
            val currencyB = Currency("USD_EUR", 0.842993).apply {
                order = 2
                isSelected = true
            }
            val areEqual = currencyA.deepEquals(currencyB)
            assertFalse(areEqual)
        }
    }

    @Nested
    inner class TrimmedCurrencyCode {
        @Test
        fun trimmedCurrencyCodeShouldReturnCorrectResult() {
            val currency = Currency("USD_EUR", 0.842993)
            val expected = "EUR"
            val actual = currency.trimmedCurrencyCode
            assertEquals(expected, actual)
        }
    }

    @Nested
    inner class ToString {
        @Test
        fun currencyShouldReturnCorrectString() {
            val currency = Currency("USD_EUR", 0.842993)
            val expected = "{-1 EUR    }"
            val actual = currency.toString()
            assertEquals(expected, actual)
        }

        @Test
        fun focusedAndSelectedCurrencyShouldReturnCorrectString() {
            val currency = Currency("USD_EUR", 0.842993).apply {
                order = 7
                isSelected = true
                isFocused = true
            }
            val expected = "{7 EUR F S}"
            val actual = currency.toString()
            assertEquals(expected, actual)
        }

        @Test
        fun focusedCurrencyShouldReturnCorrectString() {
            val currency = Currency("USD_EUR", 0.842993).apply {
                order = 7
                isFocused = true
            }
            val expected = "{7 EUR F  }"
            val actual = currency.toString()
            assertEquals(expected, actual)
        }

        @Test
        fun selectedCurrencyShouldReturnCorrectString() {
            val currency = Currency("USD_EUR", 0.842993).apply {
                order = 7
                isSelected = true
            }
            val expected = "{7 EUR   S}"
            val actual = currency.toString()
            assertEquals(expected, actual)
        }
    }
}