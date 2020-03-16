package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.*
import kotlin.properties.Delegates

class ActiveCurrenciesViewModel_kt(application: Application) : AndroidViewModel(application) {

    // Candidate for dependency injection
    private val repository = Repository(application)

    private val _activeCurrencies = repository.getActiveCurrencies()
    val activeCurrencies: LiveData<MutableList<Currency>>
        get() = _activeCurrencies

    private fun upsertCurrency(currency: Currency?) = repository.upsertCurrency(currency)

    private suspend fun getCurrency(currencyCode: String) = repository.getCurrency(currencyCode)

    private fun isFirstLaunch() = repository.isFirstLaunch
    private fun setFirstLaunch(value: Boolean) {
        repository.isFirstLaunch = value
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
        swapCurrencies(oldPosition, newPosition)
    }

    fun handleDrop() {
        adapterActiveCurrencies.forEach {
            upsertCurrency(it)
        }
    }

    private fun swapCurrencies(oldPosition: Int, newPosition: Int) {
        adapterActiveCurrencies[oldPosition].order = adapterActiveCurrencies[newPosition].order.also {
            adapterActiveCurrencies[newPosition].order = adapterActiveCurrencies[oldPosition].order
        }
        adapterActiveCurrencies[oldPosition] = adapterActiveCurrencies[newPosition].also {
            adapterActiveCurrencies[newPosition] = adapterActiveCurrencies[oldPosition]
        }
    }

    fun populateDefaultCurrencies() {
        if (!isFirstLaunch()) return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                setFirstLaunch(false)
                val localCurrencyCode = "USD_${java.util.Currency.getInstance(Locale.getDefault()).currencyCode}"
                val defaultCurrencies = mutableSetOf<Currency?>()
                val usd = getCurrency("USD_USD")
                usd?.order = 0
                usd?.isSelected = true
                defaultCurrencies.add(usd)
                val localCurrency = getCurrency(localCurrencyCode)
                if (localCurrencyCode == "USD_USD" || localCurrency == null) {
                    val eur = getCurrency("USD_EUR")
                    eur?.order = 1
                    eur?.isSelected = true
                    val jpy = getCurrency("USD_JPY")
                    jpy?.order = 2
                    jpy?.isSelected = true
                    val gbp = getCurrency("USD_GBP")
                    gbp?.order = 3
                    gbp?.isSelected = true
                    defaultCurrencies.addAll(listOf(eur, jpy, gbp))
                } else {
                    localCurrency.order = 1
                    localCurrency.isSelected = true
                    defaultCurrencies.add(localCurrency)
                }
                defaultCurrencies.forEach {
                    upsertCurrency(it)
                }
            }
        }
    }
}