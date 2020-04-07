package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.Order.*
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.elementAfter
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.elementBefore
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.hasOneElement
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isNotLastElement
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*
import kotlin.properties.Delegates

class ActiveCurrenciesViewModel(application: Application) : AndroidViewModel(application) {

    // Candidate for dependency injection
    private val repository = Repository(application)

    val activeCurrencies = repository.getActiveCurrencies()

    private fun upsertCurrency(currency: Currency?) = repository.upsertCurrency(currency)

    private suspend fun getCurrency(currencyCode: String) = repository.getCurrency(currencyCode)

    private fun isFirstLaunch() = repository.isFirstLaunch
    private fun setFirstLaunch(value: Boolean) {
        repository.isFirstLaunch = value
    }

    var adapterActiveCurrencies = mutableListOf<Currency>()
    val focusedCurrency = MutableLiveData<Currency?>()

    private lateinit var swipedCurrency: Currency
    private var swipedCurrencyOrder by Delegates.notNull<Int>()

    fun handleSwipe(position: Int): Int {
        val indexToRefresh = reassignFocusedCurrency(position)
        shiftCurrencies(position)
        return indexToRefresh
    }

    /**
     * Here we are reassigning the focus to another currency if the focused currency is swiped.
     * The assignment logic is:
     *      If there are items below me
     *          Unfocus me, focus the item directly below me
     *          Return position of item directly below me
     *      Else if I am the sole item
     *          Unfocus me
     *          Return invalid position
     *      Else
     *          Unfocus me, focus the item directly above me
     *          Return position of time directly above me
     */
    private fun reassignFocusedCurrency(position: Int): Int {
        swipedCurrency = adapterActiveCurrencies[position]
        if (focusedCurrency.value == swipedCurrency) {
            when {
                adapterActiveCurrencies.isNotLastElement(position) -> {
                    adapterActiveCurrencies.elementAfter(position).let { newlyFocusedCurrency ->
                        newlyFocusedCurrency.isFocused = true
                        swipedCurrency.isFocused = false
                        focusedCurrency.value = newlyFocusedCurrency
                        return adapterActiveCurrencies.indexOf(newlyFocusedCurrency)
                    }
                }
                adapterActiveCurrencies.hasOneElement() -> {
                    focusedCurrency.value = null
                    return INVALID.position
                }
                else -> {
                    adapterActiveCurrencies.elementBefore(position).let { newlyFocusedCurrency ->
                        newlyFocusedCurrency.isFocused = true
                        swipedCurrency.isFocused = false
                        focusedCurrency.value = newlyFocusedCurrency
                        return adapterActiveCurrencies.indexOf(newlyFocusedCurrency)
                    }
                }
            }
        }
        return INVALID.position
    }

    private fun shiftCurrencies(position: Int) {
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
        swipedCurrency.conversion.conversionValue = BigDecimal.ZERO
        upsertCurrency(swipedCurrency)
    }

    /**
     * Makes the [newlyFocusedCurrency] focused and the previously focused currency unfocused.
     * If the previously focused currency was swiped it will not be in the list and the index will
     * return -1.
     */
    fun changeFocusedCurrency(newlyFocusedCurrency: Currency): Int {
        val indexOfPreviouslyFocusedCurrency = adapterActiveCurrencies.indexOf(focusedCurrency.value)
        if (indexOfPreviouslyFocusedCurrency.isValid()) {
            adapterActiveCurrencies[indexOfPreviouslyFocusedCurrency].isFocused = false
        }
        focusedCurrency.value = newlyFocusedCurrency
        newlyFocusedCurrency.isFocused = true
        return indexOfPreviouslyFocusedCurrency
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
        viewModelScope.launch(Dispatchers.IO) {
            setFirstLaunch(false)
            val localCurrencyCode = "USD_${java.util.Currency.getInstance(Locale.getDefault()).currencyCode}"
            getCurrency("USD_USD")?.run {
                setDefaultCurrency(this, FIRST.position)
            }
            val localCurrency = getCurrency(localCurrencyCode)
            if (localCurrencyCode == "USD_USD" || localCurrency == null) {
                getCurrency("USD_EUR")?.run {
                    setDefaultCurrency(this, SECOND.position)
                }
                getCurrency("USD_JPY")?.run {
                    setDefaultCurrency(this, THIRD.position)
                }
                getCurrency("USD_GBP")?.run {
                    setDefaultCurrency(this, FOURTH.position)
                }
            } else {
                localCurrency.run {
                    setDefaultCurrency(this, SECOND.position)
                }
            }
        }
    }

    private fun setDefaultCurrency(currency: Currency, order: Int) {
        currency.order = order
        currency.isSelected = true
        upsertCurrency(currency)
    }
}