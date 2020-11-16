package com.nicoqueijo.android.currencyconverter.kotlin.data

import android.content.Context
import com.google.common.truth.Truth.assertThat
import com.nicoqueijo.android.currencyconverter.kotlin.model.ApiEndPoint
import com.nicoqueijo.android.currencyconverter.kotlin.model.Resource
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isNetworkAvailable
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import retrofit2.Response
import java.net.SocketTimeoutException

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class DefaultRepositoryTest {

    private val context = mockk<Context>(relaxed = true)
    private val exchangeRateService = mockk<ExchangeRateService>(relaxed = true)
    private val currencyDao = mockk<CurrencyDao>(relaxed = true)
    private val appPrefs = mockk<AppPrefs>(relaxed = true)
    private val repository: Repository = DefaultRepository(
            context,
            exchangeRateService,
            currencyDao,
            appPrefs
    )

    @Nested
    inner class FetchCurrencies {
        @Test
        fun whenNetworkIsAvailableAndDataIsEmptyShouldReturnSuccess() = runBlocking {
            val spy = spyk(repository)
            every { context.isNetworkAvailable() } returns true
            every { appPrefs.isDataEmpty } returns true
            every { appPrefs.isDataStale } returns false
            coEvery { exchangeRateService.getExchangeRates(any()) } returns Response.success(null)
            val expected = Resource.Success
            val actual = spy.fetchCurrencies()
            verify { spy["persistResponse"](any<Response<ApiEndPoint>>()) }
            assertThat(actual).isInstanceOf(expected.javaClass)
        }

        @Test
        fun whenNetworkIsAvailableAndDataIsStaleShouldReturnSuccess() = runBlocking {
            val spy = spyk(repository, recordPrivateCalls = true)
            every { context.isNetworkAvailable() } returns true
            every { appPrefs.isDataEmpty } returns false
            every { appPrefs.isDataStale } returns true
            coEvery { exchangeRateService.getExchangeRates(any()) } returns Response.success(null)
            val expected = Resource.Success
            val actual = spy.fetchCurrencies()
            verify { spy["persistResponse"](any<Response<ApiEndPoint>>()) }
            assertThat(actual).isInstanceOf(expected.javaClass)
        }

        @Test
        fun whenNetworkIsAvailableAndDataIsNotEmptyAndDataIsNotStaleShouldReturnSuccess() = runBlocking {
            every { context.isNetworkAvailable() } returns true
            every { appPrefs.isDataEmpty } returns false
            every { appPrefs.isDataStale } returns false
            val expected = Resource.Success
            val actual = repository.fetchCurrencies()
            assertThat(actual).isInstanceOf(expected.javaClass)
        }

        @Test
        fun whenNetworkIsUnavailableAndDataIsEmptyShouldReturnError() = runBlocking {
            every { context.isNetworkAvailable() } returns false
            every { appPrefs.isDataEmpty } returns true
            every { appPrefs.isDataStale } returns false
            val expected = Resource.Error(DefaultRepository.NETWORK_OR_DATA_UNAVAILABLE_ERROR_MESSAGE)
            val actual = repository.fetchCurrencies()
            assertThat(actual).isInstanceOf(expected.javaClass)
            assertThat((actual as Resource.Error).message).isEqualTo(expected.message)
        }

        @Test
        fun whenNetworkIsUnavailableAndDataIsNotEmptyShouldReturnSuccess() = runBlocking {
            every { context.isNetworkAvailable() } returns false
            every { appPrefs.isDataEmpty } returns false
            every { appPrefs.isDataStale } returns true
            val expected = Resource.Success
            val actual = repository.fetchCurrencies()
            assertThat(actual).isInstanceOf(expected.javaClass)
        }

        @Test
        fun whenNetworkIsAvailableButRetrofitCallTimesOutShouldReturnError() = runBlocking {
            every { context.isNetworkAvailable() } returns true
            every { appPrefs.isDataEmpty } returns true
            every { appPrefs.isDataStale } returns false
            coEvery { exchangeRateService.getExchangeRates(any()) } throws SocketTimeoutException()
            val expected = Resource.Error(DefaultRepository.NETWORK_TIMEOUT_ERROR_MESSAGE)
            val actual = repository.fetchCurrencies()
            assertThat(actual).isInstanceOf(expected.javaClass)
            assertThat((actual as Resource.Error).message).isEqualTo(expected.message)
        }
    }
}