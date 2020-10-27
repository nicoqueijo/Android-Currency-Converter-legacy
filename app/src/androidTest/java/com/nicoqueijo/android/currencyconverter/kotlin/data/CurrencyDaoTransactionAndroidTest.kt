package com.nicoqueijo.android.currencyconverter.kotlin.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nicoqueijo.android.currencyconverter.InstantTaskExecutorExtension
import com.nicoqueijo.android.currencyconverter.getOrAwaitValue
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.Executors

/**
 * Unfortunately I cannot test @Transaction-annoted functions and LiveData-returning functions
 * in the same class.
 * See: https://stackoverflow.com/questions/57027850/testing-android-room-with-livedata-coroutines-and-transactions/61044052#61044052
 */
@ExperimentalCoroutinesApi
@ExtendWith(InstantTaskExecutorExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class CurrencyDaoTransactionAndroidTest {

    private lateinit var currencyDatabase: CurrencyDatabase
    private lateinit var currencyDao: CurrencyDao

    @BeforeEach
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        currencyDatabase = Room.inMemoryDatabaseBuilder(
                context, CurrencyDatabase::class.java)
                .setTransactionExecutor(Executors.newSingleThreadExecutor())
                .build()
        currencyDao = currencyDatabase.currencyDao()
    }

    @AfterEach
    fun tearDown() {
        currencyDatabase.close()
    }

    @Nested
    inner class UpsertCurrencies {
        @Test
        fun insertingMultipleCurrenciesInTheDatabaseShouldSucceed() = runBlocking {
            val currencies = listOf(
                    Currency("USD_USD", 1.0),
                    Currency("USD_EUR", 0.842993),
                    Currency("USD_GBP", 0.766548),
                    Currency("USD_JPY", 104.70502716),
                    Currency("USD_BTC", 0.000076918096),
                    Currency("USD_XAU", 0.00052584),
                    Currency("USD_ARS", 78.120127)
            )
            currencyDao.upsertCurrencies(currencies)
            val databaseCurrencies = currencyDao.getAllCurrencies().getOrAwaitValue()
            val areSame = databaseCurrencies.containsAll(currencies) &&
                    currencies.containsAll(databaseCurrencies)
            assertTrue(areSame)
        }
    }

    @Nested
    inner class UpdateExchangeRates {
        @Test
        fun updatingMultipleExchangeRatesInTheDatabaseShouldSucceed() = runBlocking {
            val currencyEUR = Currency("USD_EUR", 0.842993)
            val currencyJPY = Currency("USD_JPY", 104.70502716)
            val currencyARS = Currency("USD_ARS", 78.120127)
            val currencies = listOf(currencyEUR, currencyJPY, currencyARS)
            currencyDao.upsertCurrencies(currencies)
            val modifiedCurrencyEUR = currencyEUR.copy(exchangeRate = 0.8215)
            val modifiedCurrencyJPY = currencyJPY.copy(exchangeRate = 110.05569211)
            val modifiedCurrencyARS = currencyARS.copy(exchangeRate = 69.42875)
            val modifiedCurrencies = listOf(modifiedCurrencyEUR, modifiedCurrencyJPY, modifiedCurrencyARS)
            currencyDao.updateExchangeRates(modifiedCurrencies)
            val databaseCurrencies = currencyDao.getAllCurrencies().getOrAwaitValue()
            val databaseCurrencyEUR = databaseCurrencies.find { it.currencyCode == "USD_EUR" }!!
            val databaseCurrencyJPY = databaseCurrencies.find { it.currencyCode == "USD_JPY" }!!
            val databaseCurrencyARS = databaseCurrencies.find { it.currencyCode == "USD_ARS" }!!
            assertEquals(modifiedCurrencyEUR.exchangeRate, databaseCurrencyEUR.exchangeRate)
            assertEquals(modifiedCurrencyJPY.exchangeRate, databaseCurrencyJPY.exchangeRate)
            assertEquals(modifiedCurrencyARS.exchangeRate, databaseCurrencyARS.exchangeRate)
        }
    }
}