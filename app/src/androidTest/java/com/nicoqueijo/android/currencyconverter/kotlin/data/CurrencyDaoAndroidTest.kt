package com.nicoqueijo.android.currencyconverter.kotlin.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nicoqueijo.android.currencyconverter.InstantTaskExecutorExtension
import com.nicoqueijo.android.currencyconverter.getOrAwaitValue
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.deepEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(InstantTaskExecutorExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class CurrencyDaoAndroidTest {

    private lateinit var currencyDatabase: CurrencyDatabase
    private lateinit var currencyDao: CurrencyDao

    @BeforeEach
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        currencyDatabase = Room.inMemoryDatabaseBuilder(
                context, CurrencyDatabase::class.java)
                .build()
        currencyDao = currencyDatabase.currencyDao()
    }

    @AfterEach
    fun tearDown() {
        currencyDatabase.close()
    }

    @Nested
    inner class UpsertCurrency {
        @Test
        fun insertingACurrencyInTheDatabaseShouldSucceed() = runBlockingTest {
            val insertedCurrency = Currency("USD_EUR", 0.842993)
            currencyDao.upsertCurrency(insertedCurrency)
            val retrievedCurrency = currencyDao.getCurrency("USD_EUR")
            assertEquals(insertedCurrency, retrievedCurrency)
        }

        @Test
        fun insertingCurrencyThatIsAlreadyInTheDatabaseShouldBeReplaced() = runBlockingTest {
            val originalCurrency = Currency("USD_EUR", 0.842993)
            currencyDao.upsertCurrency(originalCurrency)
            val repeatedCurrency = Currency("USD_EUR", 0.865147)
            currencyDao.upsertCurrency(repeatedCurrency)
            val expected = 1
            val actual = currencyDao.getAllCurrencies().getOrAwaitValue().size
            assertEquals(expected, actual)
        }
    }

    @Nested
    inner class UpdateExchangeRate {
        @Test
        fun updatingAnExchangeRateInTheDatabaseShouldSucceed() = runBlockingTest {
            val currency = Currency("USD_EUR", 0.842993)
            currencyDao.upsertCurrency(currency)
            val modifiedCurrency = currency.copy(exchangeRate = 0.865147)
            currencyDao.updateExchangeRate(modifiedCurrency.currencyCode, modifiedCurrency.exchangeRate)
            val databaseCurrency = currencyDao.getCurrency("USD_EUR")
            assertEquals(databaseCurrency.exchangeRate, modifiedCurrency.exchangeRate)
        }
    }

    @Nested
    inner class GetCurrency {
        @Test
        fun gettingACurrencyFromTheDatabaseShouldSucceed() = runBlockingTest {
            val currency = Currency("USD_EUR", 0.842993)
            currencyDao.upsertCurrency(currency)
            val databaseCurrency = currencyDao.getCurrency("USD_EUR")
            assertEquals(currency, databaseCurrency)
        }

        @Test
        fun gettingANonExistentCurrencyFromTheDatabaseShouldFail() = runBlockingTest {
            val currencyEUR = Currency("USD_EUR", 0.842993)
            currencyDao.upsertCurrency(currencyEUR)
            val databaseCurrency = currencyDao.getCurrency("USD_ARS")
            assertNull(databaseCurrency)
        }
    }

    @Nested
    inner class GetAllCurrencies {
        @Test
        fun gettingAllCurrenciesFromTheDatabaseShouldSucceed() = runBlockingTest {
            val currencyBTC = Currency("USD_BTC", 0.000076918096).apply {
                order = 0
                isSelected = true
            }
            val currencyARS = Currency("USD_ARS", 78.120127).apply {
                order = 1
                isSelected = true
            }
            val currencyEUR = Currency("USD_EUR", 0.842993).apply {
                order = 2
                isSelected = true
            }
            val currencyXAU = Currency("USD_XAU", 0.00052584).apply {
                order = 3
                isSelected = true
            }
            val currencyUSD = Currency("USD_USD", 1.0)
            val currencyGBP = Currency("USD_GBP", 0.766548)
            val currencyJPY = Currency("USD_JPY", 104.70502716)
            val currencies = listOf(
                    currencyBTC,
                    currencyARS,
                    currencyEUR,
                    currencyXAU,
                    currencyUSD,
                    currencyGBP,
                    currencyJPY
            )
            currencyDao.run {
                upsertCurrency(currencyUSD)
                upsertCurrency(currencyEUR)
                upsertCurrency(currencyGBP)
                upsertCurrency(currencyJPY)
                upsertCurrency(currencyBTC)
                upsertCurrency(currencyXAU)
                upsertCurrency(currencyARS)
            }
            val databaseCurrencies = currencyDao.getAllCurrencies().getOrAwaitValue()
            val areSame = databaseCurrencies.containsAll(currencies) &&
                    currencies.containsAll(databaseCurrencies)
            assertTrue(areSame)
        }

        @Test
        fun gettingAllCurrenciesFromEmptyTableInTheDatabaseShouldYieldNoResults() = runBlockingTest {
            val databaseCurrencies = currencyDao.getAllCurrencies().getOrAwaitValue()
            assertTrue(databaseCurrencies.isEmpty())
        }
    }

    @Nested
    inner class GetActiveCurrencies {
        @Test
        fun gettingActiveCurrenciesFromTheDatabaseShouldSucceed() = runBlockingTest {
            val currencyGBP = Currency("USD_GBP", 0.766548)
            val currencyBTC = Currency("USD_BTC", 0.000076918096).apply {
                order = 0
                isSelected = true
            }
            val currencyXAU = Currency("USD_XAU", 0.00052584).apply {
                order = 3
                isSelected = true
            }
            val currencyUSD = Currency("USD_USD", 1.0)
            val currencyARS = Currency("USD_ARS", 78.120127).apply {
                order = 1
                isSelected = true
            }
            val currencyJPY = Currency("USD_JPY", 104.70502716)
            val currencyEUR = Currency("USD_EUR", 0.842993).apply {
                order = 2
                isSelected = true
            }
            val currencies = listOf(
                    currencyGBP,
                    currencyBTC,
                    currencyXAU,
                    currencyUSD,
                    currencyARS,
                    currencyJPY,
                    currencyEUR
            )
            currencyDao.run {
                upsertCurrency(currencyGBP)
                upsertCurrency(currencyBTC)
                upsertCurrency(currencyXAU)
                upsertCurrency(currencyUSD)
                upsertCurrency(currencyARS)
                upsertCurrency(currencyJPY)
                upsertCurrency(currencyEUR)
            }
            val sortedActiveCurrencies = currencies.asSequence()
                    .filter { it.isSelected }
                    .sortedBy { it.order }
                    .toList()
            val databaseCurrencies = currencyDao.getActiveCurrencies().getOrAwaitValue()
            val areListsEqual = databaseCurrencies.deepEquals(sortedActiveCurrencies)
            assertTrue(areListsEqual)
        }

        @Test
        fun gettingActiveCurrenciesFromEmptyTableInTheDatabaseShouldYieldNoResults() = runBlockingTest {
            val databaseCurrencies = currencyDao.getActiveCurrencies().getOrAwaitValue()
            assertTrue(databaseCurrencies.isEmpty())
        }
    }
}