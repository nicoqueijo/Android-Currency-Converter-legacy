package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.view.ActiveCurrenciesFragment_kt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        Log.d(ActiveCurrenciesFragment_kt.TAG, "Swiped currency: $swipedCurrency")
        swipedCurrencyOrder = swipedCurrency.order
        Log.d(ActiveCurrenciesFragment_kt.TAG, "Swiped currency order: $swipedCurrencyOrder")
//        val conversionValue = swipedCurrency.conversionValue

        Log.d(ActiveCurrenciesFragment_kt.TAG, "Active currencies: $adapterActiveCurrencies")
        run loop@{
            adapterActiveCurrencies
                    .reversed()
                    .forEach { currency ->
                        if (currency.order == swipedCurrencyOrder) {
                            return@loop
                        }
                        currency.order--
                        Log.d(ActiveCurrenciesFragment_kt.TAG, "Shifting (swipe): $currency")
                        upsertCurrency(currency)
                    }
        }
        swipedCurrency.isSelected = false
        swipedCurrency.order = -1
        swipedCurrency.conversionValue = BigDecimal(0.0)
        Log.d(ActiveCurrenciesFragment_kt.TAG, "Swiped: $swipedCurrency")
        upsertCurrency(swipedCurrency)
        Log.d(ActiveCurrenciesFragment_kt.TAG, "activeCurrencies after Swipe: $adapterActiveCurrencies")

    }

    fun handleSwipeUndo() {
        //            swipedCurrency.conversionValue = conversionValue
        swipedCurrency.isSelected = true
        swipedCurrency.order = swipedCurrencyOrder
        Log.d(ActiveCurrenciesFragment_kt.TAG, "Recovered: $swipedCurrency")
        upsertCurrency(swipedCurrency)

        for (i in swipedCurrencyOrder until adapterActiveCurrencies.size) {
            val currency = adapterActiveCurrencies[i]
            currency.order++
            Log.d(ActiveCurrenciesFragment_kt.TAG, "Shifting (undo): $currency")
            upsertCurrency(currency)
        }
//            notifyItemInserted(position)
    }

    fun handleMove(oldPosition: Int, newPosition: Int) {
        // a = b.also { b = a }
//        adapterActiveCurrencies[oldPosition] = adapterActiveCurrencies[newPosition].also {
//            adapterActiveCurrencies[newPosition] = adapterActiveCurrencies[oldPosition]
//        }
        adapterActiveCurrencies[oldPosition].order = adapterActiveCurrencies[newPosition].order.also {
            adapterActiveCurrencies[newPosition].order = adapterActiveCurrencies[oldPosition].order
        }
        Log.d("HandleMove", "handle move called: Old Position: $oldPosition, New Position: $newPosition")

//        upsertCurrency(adapterActiveCurrencies[oldPosition])
//        upsertCurrency(adapterActiveCurrencies[newPosition])

//        val movingCurrency = adapterActiveCurrencies[oldPosition]
//
//        adapterActiveCurrencies.removeAt(oldPosition)
//
//        adapterActiveCurrencies.add(newPosition, movingCurrency)
    }
}