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
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.hasOnlyOneElement
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isNotLastElement
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ActiveCurrenciesViewModel(application: Application) : AndroidViewModel(application) {

    var wasListConstructed = false

    // Candidate for dependency injection
    private val repository = Repository(application)

    val databaseActiveCurrencies = repository.getActiveCurrencies()

    private fun upsertCurrency(currency: Currency?) = repository.upsertCurrency(currency)

    private suspend fun getCurrency(currencyCode: String) = repository.getCurrency(currencyCode)

    /*fun getActiveCurrencies() = repository.getActiveCurrencies().value*/

    private fun isFirstLaunch() = repository.isFirstLaunch
    private fun setFirstLaunch(value: Boolean) {
        repository.isFirstLaunch = value
    }

    val memoryActiveCurrencies = mutableListOf<Currency>()
    val focusedCurrency = MutableLiveData<Currency?>()

    /*private*/ var decimalFormatter: DecimalFormat
    /*private*/ var decimalSeparator: String
    /*private*/ var groupingSeparator: String

    init {
        val numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault())
        val conversionPattern = "###,##0.0000"
        decimalFormatter = numberFormatter as DecimalFormat
        decimalFormatter.applyPattern(conversionPattern)
        groupingSeparator = decimalFormatter.decimalFormatSymbols.groupingSeparator.toString()
        decimalSeparator = decimalFormatter.decimalFormatSymbols.decimalSeparator.toString()
    }

    /**
     * Makes the currency at [positionOfClickedCurrency] focused and the previously focused
     * currency unfocused. If the previously focused currency was removed it will not be in the
     * list and the position will invalid.
     */
    fun changeFocusedCurrency(positionOfClickedCurrency: Int) {
        val clickedCurrency = memoryActiveCurrencies[positionOfClickedCurrency]
        val positionOfPreviouslyFocusedCurrency = memoryActiveCurrencies.indexOf(focusedCurrency.value)
        if (positionOfPreviouslyFocusedCurrency.isValid()) {
            memoryActiveCurrencies[positionOfPreviouslyFocusedCurrency].isFocused = false
        }
        focusedCurrency.value = clickedCurrency
        clickedCurrency.isFocused = true
    }

    /**
     * Sets the focus to the first Currency if the list is not empty.
     */
    fun setDefaultFocus() {
        if (focusedCurrency.value == null && memoryActiveCurrencies.isNotEmpty()) {
            focusedCurrency.value = memoryActiveCurrencies.take(1).single().also { firstCurrency ->
                firstCurrency.isFocused = true
            }
        }
    }

    fun handleRemove(currencyToRemove: Currency, positionOfCurrencyToRemove: Int) {
        shiftCurrenciesUp(positionOfCurrencyToRemove)
        reassignFocusedCurrency(positionOfCurrencyToRemove)
        currencyToRemove.isSelected = false
        currencyToRemove.order = INVALID.position
        upsertCurrency(currencyToRemove)
        memoryActiveCurrencies.remove(currencyToRemove)
    }

    fun handleUndo(currencyToRestore: Currency, positionOfCurrencyToRestore: Int) {
        currencyToRestore.isSelected = true
        currencyToRestore.order = positionOfCurrencyToRestore
        upsertCurrency(currencyToRestore)
        for (i in positionOfCurrencyToRestore until memoryActiveCurrencies.size) {
            memoryActiveCurrencies[i].let {
                it.order++
                upsertCurrency(it)
            }
        }
        memoryActiveCurrencies.add(positionOfCurrencyToRestore, currencyToRestore)
    }

    /**
     * Here we are reassigning the focus to another currency if the focused currency is removed.
     * The reassignment logic is as follows:
     *      If there are items below me:
     *          Unfocus me, focus the item directly below me.
     *      Else if I am the sole item:
     *          Unfocus me;
     *      Else:
     *          Unfocus me, focus the item directly above me.
     */
    private fun reassignFocusedCurrency(positionOfLongClickedCurrency: Int) {
        val removedCurrency = memoryActiveCurrencies[positionOfLongClickedCurrency]
        if (focusedCurrency.value == removedCurrency) {
            when {
                memoryActiveCurrencies.isNotLastElement(positionOfLongClickedCurrency) -> {
                    memoryActiveCurrencies.elementAfter(positionOfLongClickedCurrency).let { newlyFocusedCurrency ->
                        newlyFocusedCurrency.isFocused = true
                        removedCurrency.isFocused = false
                        focusedCurrency.value = newlyFocusedCurrency
                    }
                }
                memoryActiveCurrencies.hasOnlyOneElement() -> {
                    focusedCurrency.value = null
                }
                else -> {
                    memoryActiveCurrencies.elementBefore(positionOfLongClickedCurrency).let { newlyFocusedCurrency ->
                        newlyFocusedCurrency.isFocused = true
                        removedCurrency.isFocused = false
                        focusedCurrency.value = newlyFocusedCurrency
                    }
                }
            }
        }
    }

    /**
     * All the currencies below the removed currency are shifted up one position to fill in the gap.
     */
    private fun shiftCurrenciesUp(orderOfCurrencyToRemove: Int) {
        run loop@{
            memoryActiveCurrencies
                    .reversed()
                    .forEach { currency ->
                        if (currency.order == orderOfCurrencyToRemove) {
                            return@loop
                        }
                        currency.order--
                        upsertCurrency(currency)
                    }
        }
    }

    /**
     * This indicates the user added a currency by selecting it from the SelectableCurrenciesFragment
     * that was initiated by the press of the FloatingActionButton.
     * The indication is triggered when, the only difference between [databaseActiveCurrencies] and
     * [memoryActiveCurrencies] is that [databaseActiveCurrencies] has an extra element.
     */
    fun wasCurrencyAddedViaFab(databaseActiveCurrencies: List<Currency>) =
            (databaseActiveCurrencies.size - memoryActiveCurrencies.size == 1 &&
                    databaseActiveCurrencies.dropLast(1) == memoryActiveCurrencies)

    /**
     * On the drag events, adjacent currencies need to swap position indices and this needs to be
     * reflected in memory and in the database.
     */
    fun swapCurrencies(firstPosition: Int, secondPosition: Int) {
        memoryActiveCurrencies.run {
            this[firstPosition].order = this[secondPosition].order.also {
                this[secondPosition].order = this[firstPosition].order
            }
            this[firstPosition] = this[secondPosition].also {
                this[secondPosition] = this[firstPosition]
            }
            upsertCurrency(this[firstPosition])
            upsertCurrency(this[secondPosition])
        }
    }

    fun initDefaultCurrencies() {
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