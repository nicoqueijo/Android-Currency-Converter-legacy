package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import java.math.BigDecimal
import kotlin.properties.Delegates

class ActiveCurrenciesViewModel_kt(application: Application) : AndroidViewModel(application) {

    // Candidate for dependency injection
    private val repository = Repository(application)
    private val _activeCurrencies = repository.getActiveCurrencies()
    val activeCurrencies: LiveData<MutableList<Currency>>
        get() = _activeCurrencies

    private fun upsertCurrency(currency: Currency) {
        repository.upsertCurrency(currency)
    }

    var adapterActiveCurrencies = mutableListOf<Currency>()

    var fabClicks: Int = 0

    private lateinit var swipedCurrency: Currency
    private var swipedCurrencyOrder by Delegates.notNull<Int>()

    fun handleSwipe(position: Int) {
        swipedCurrency = adapterActiveCurrencies[position]
        swipedCurrencyOrder = swipedCurrency.order
//        val conversionValue = swipedCurrency.conversionValue
        run loop@{
            adapterActiveCurrencies
                    .reversed()
                    .forEach { currency ->
                        if (currency.order == swipedCurrencyOrder) {
                            return@loop
                        }
                        currency.order--
                        upsertCurrency(currency)
                    }
        }
        swipedCurrency.isSelected = false
        swipedCurrency.order = -1
        swipedCurrency.conversionValue = BigDecimal(0.0)
        upsertCurrency(swipedCurrency)
    }

    fun handleSwipeUndo() {
//        swipedCurrency.conversionValue = conversionValue
        swipedCurrency.isSelected = true
        swipedCurrency.order = swipedCurrencyOrder
        upsertCurrency(swipedCurrency)

        for (i in swipedCurrencyOrder until adapterActiveCurrencies.size) {
            val currency = adapterActiveCurrencies[i]
            currency.order++
            upsertCurrency(currency)
        }
    }

    fun handleMove(oldPosition: Int, newPosition: Int) {
        adapterActiveCurrencies[oldPosition].order = adapterActiveCurrencies[newPosition].order.also {
            adapterActiveCurrencies[newPosition].order = adapterActiveCurrencies[oldPosition].order
        }
        upsertCurrency(adapterActiveCurrencies[oldPosition])
        upsertCurrency(adapterActiveCurrencies[newPosition])
    }
}