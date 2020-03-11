package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.app.Application
import android.util.Log
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

        adapterActiveCurrencies.reversed()
                .forEach { currency ->
                    if (currency.order == swipedCurrencyOrder) {
                        return@forEach
                    }
                    currency.order--
                    Log.d("Nico", "Shifting (swipe): $currency")
                    upsertCurrency(currency)
                }

        swipedCurrency.isSelected = false
        swipedCurrency.order = -1
        swipedCurrency.conversionValue = BigDecimal(0.0)
        Log.d("Nico", "Swiped: $swipedCurrency")
        upsertCurrency(swipedCurrency)
        Log.d("Nico", "activeCurrencies after Swipe: $adapterActiveCurrencies")

    }

    fun handleSwipeUndo() {
        //            swipedCurrency.conversionValue = conversionValue
        swipedCurrency.isSelected = true
        swipedCurrency.order = swipedCurrencyOrder
        Log.d("Nico", "Recovered: $swipedCurrency")
        upsertCurrency(swipedCurrency)

        for (i in swipedCurrencyOrder until adapterActiveCurrencies.size) {
            val currency = adapterActiveCurrencies[i]
            currency.order++
            Log.d("Nico", "Shifting (undo): $currency")
            upsertCurrency(currency)
        }
        Log.d("Nico", "activeCurrencies after Undo: $adapterActiveCurrencies")

//            notifyItemInserted(position)
    }

    fun handleMove(oldPosition: Int, newPosition: Int) {
        val movingCurrency: Currency = adapterActiveCurrencies[oldPosition]
        adapterActiveCurrencies.removeAt(oldPosition)
        adapterActiveCurrencies.add(newPosition, movingCurrency)
    }
}