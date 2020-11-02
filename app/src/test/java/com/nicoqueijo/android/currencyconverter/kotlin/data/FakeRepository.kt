package com.nicoqueijo.android.currencyconverter.kotlin.data

import androidx.lifecycle.LiveData
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.model.Resource

class FakeRepository : Repository {

    override var isFirstLaunch: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}

    override val timestamp: Long
        get() = TODO("Not yet implemented")

    override fun getAllCurrencies(): LiveData<MutableList<Currency>> {
        TODO("Not yet implemented")
    }

    override fun getSelectedCurrencies(): LiveData<MutableList<Currency>> {
        TODO("Not yet implemented")
    }

    override fun upsertCurrency(currency: Currency) {
        TODO("Not yet implemented")
    }

    override fun upsertCurrencies(currencies: List<Currency>) {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrency(currencyCode: String): Currency {
        TODO("Not yet implemented")
    }

    override suspend fun fetchCurrencies(): Resource {
        TODO("Not yet implemented")
    }
}