package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.CurrencyConversion
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.EMPTY
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.Order.*
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.elementAfter
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.elementBefore
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.hasOnlyOneElement
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isNotLastElement
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isValid
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.roundToFourDecimalPlaces
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*

@ActivityRetainedScoped
class WatchlistViewModel @ViewModelInject constructor(
        private val repository: Repository,
        application: Application) : AndroidViewModel(application) {

    private fun upsertCurrency(currency: Currency) = repository.upsertCurrency(currency)
    private fun upsertCurrencies(currencies: List<Currency>) = repository.upsertCurrencies(currencies)
    private suspend fun getCurrency(currencyCode: String) = repository.getCurrency(currencyCode)

    fun isFirstLaunch() = repository.isFirstLaunch
    fun setFirstLaunch(value: Boolean) {
        repository.isFirstLaunch = value
    }

    val databaseActiveCurrencies = repository.getActiveCurrencies()
    val memoryActiveCurrencies = mutableListOf<Currency>()
    val focusedCurrency = MutableLiveData<Currency?>()
    var wasListConstructed = false

    /**
     * If the [button] is a Button we know that belongs to chars 0-9 or the decimal
     * separator as those were declared as Buttons.
     * If the [button] is an ImageButton that can only mean that it is the backspace
     * key as that was the only one declared as an ImageButton.
     */
    fun processKeyboardInput(button: View?): Boolean {
        var existingText = focusedCurrency.value?.conversion?.conversionString
        var isInputValid = true
        when (button) {
            is Button -> {
                val keyPressed = button.text
                var input = existingText + keyPressed
                input = cleanInput(input)
                isInputValid = isInputValid(input)
            }
            is ImageButton -> {
                existingText = existingText?.dropLast(1)
                focusedCurrency.value?.conversion?.conversionString = existingText!!
            }
        }
        return isInputValid
    }

    /**
     * If input has a leading decimal separator it prefixes it with a zero.
     * If input has a leading 0 it removes it.
     * Examples:   "." -> "0."
     *            "00" -> "0"
     *            "07" -> "0"
     */
    private fun cleanInput(input: String): String {
        var cleanInput = input.replace(",", ".")
        when {
            "." == cleanInput -> cleanInput = "0."
            Regex("0[^.]").matches(cleanInput) -> cleanInput = input[SECOND.position].toString()
        }
        return cleanInput
    }

    private fun isInputValid(input: String): Boolean {
        return (validateLength(input) &&
                validateDecimalPlaces(input) &&
                validateDecimalSeparator(input))
    }

    /**
     * Assures the whole part of the input is not above 20 digits long.
     */
    private fun validateLength(input: String): Boolean {
        val maxDigitsAllowed = 20
        if (!input.contains(".") && input.length > maxDigitsAllowed) {
            focusedCurrency.value?.conversion?.conversionString = input.dropLast(1)
            return false
        }
        focusedCurrency.value?.conversion?.conversionString = input
        return true
    }

    /**
     * Assures the decimal part of the input is at most 4 digits.
     */
    private fun validateDecimalPlaces(input: String): Boolean {
        val maxDecimalPlacesAllowed = 4
        if (input.contains(".") &&
                input.substring(input.indexOf(".") + 1).length > maxDecimalPlacesAllowed) {
            focusedCurrency.value?.conversion?.conversionString = input.dropLast(1)
            return false
        }
        focusedCurrency.value?.conversion?.conversionString = input
        return true
    }

    /**
     * Assures the input contains at most 1 decimal separator.
     */
    @SuppressLint("SetTextI18n")
    private fun validateDecimalSeparator(input: String): Boolean {
        val decimalSeparatorCount = input.asSequence()
                .count { it == '.' }
        if (decimalSeparatorCount > 1) {
            focusedCurrency.value?.conversion?.conversionString = input.dropLast(1)
            return false
        }
        focusedCurrency.value?.conversion?.conversionString = input
        return true
    }

    fun clearConversions() {
        focusedCurrency.value?.conversion?.conversionString = String.EMPTY
        memoryActiveCurrencies
                .filter { it != focusedCurrency.value }
                .forEach { it.conversion.conversionString = String.EMPTY }
    }

    /**
     * Runs the conversion of all currencies against the focused currency's input value.
     */
    fun runConversions() {
        val focusedCurrency = focusedCurrency.value
        memoryActiveCurrencies
                .filter { it != focusedCurrency }
                .forEach {
                    val fromRate = focusedCurrency!!.exchangeRate
                    val toRate = it.exchangeRate
                    if (focusedCurrency.conversion.conversionString.isNotEmpty()) {
                        val conversionValue = CurrencyConversion.convertCurrency(BigDecimal(focusedCurrency
                                .conversion.conversionString.replace(",", ".")), fromRate, toRate)
                        it.conversion.conversionValue = conversionValue
                    } else {
                        it.conversion.conversionString = String.EMPTY
                    }
                }
    }

    /**
     * Updates the hints of all currencies against the focused currency with value of 1.
     */
    fun updateHints() {
        val focusedCurrency = focusedCurrency.value
        focusedCurrency!!.conversion.conversionHint = "1"
        memoryActiveCurrencies
                .filter { it != focusedCurrency }
                .forEach {
                    val fromRate = focusedCurrency.exchangeRate
                    val toRate = it.exchangeRate
                    val conversionValue = CurrencyConversion.convertCurrency(BigDecimal("1"),
                            fromRate, toRate).roundToFourDecimalPlaces().toString()
                    it.conversion.conversionHint = conversionValue
                }
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
     * Sets the focus to the first currency if the list is not empty.
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
        shiftCurrenciesDown(positionOfCurrencyToRestore)
        memoryActiveCurrencies.add(positionOfCurrencyToRestore, currencyToRestore)
    }

    /**
     * Here we are reassigning the focus to another currency if the focused currency is removed.
     * The reassignment logic is as follows:
     *      If there are items below me:
     *          Unfocus me, focus the item directly below me.
     *      Else if I am the sole item:
     *          Unfocus me.
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
     * All currencies below the restored currency are shifted down one position to make room.
     */
    private fun shiftCurrenciesDown(positionOfCurrencyToRestore: Int) {
        for (i in positionOfCurrencyToRestore until memoryActiveCurrencies.size) {
            memoryActiveCurrencies[i].let {
                it.order++
                upsertCurrency(it)
            }
        }
    }

    /**
     * This indicates the user added a currency by selecting it from the SelectableCurrenciesFragment
     * that was initiated by the click of the FloatingActionButton.
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
            upsertCurrencies(listOf(this[firstPosition], this[secondPosition]))
        }
    }

    fun removeAllCurrencies() {
        focusedCurrency.value = null
        memoryActiveCurrencies.forEach {
            it.order = INVALID.position
            it.isSelected = false
        }
        val currenciesToRemove = mutableListOf<Currency>()
        memoryActiveCurrencies.forEach {
            currenciesToRemove.add(it.copy())
        }
        upsertCurrencies(currenciesToRemove)
        memoryActiveCurrencies.clear()
    }

    /**
     * Selects a set of default currencies on the first launch of the app based on the user's locale.
     * Logic: If the user's locale is US, it selects the US dollar and the three other most used
     *        currencies which are the Euro, Japanese Yen, and British Pound.
     *        Else, it selects the US dollar the the user's local currency.
     */
    fun initDefaultCurrencies() {
        if (isFirstLaunch()) {
            viewModelScope.launch(Dispatchers.IO) {
                val localCurrencyCode = "USD_${java.util.Currency.getInstance(Locale.getDefault()).currencyCode}"
                val defaultCurrencies = mutableListOf<Currency>()
                getCurrency("USD_USD").run {
                    defaultCurrencies.add(setDefaultCurrency(this, FIRST.position))
                }
                val localCurrency = getCurrency(localCurrencyCode)
                if (localCurrencyCode == "USD_USD") {
                    getCurrency("USD_EUR").run {
                        defaultCurrencies.add(setDefaultCurrency(this, SECOND.position))
                    }
                    getCurrency("USD_JPY").run {
                        defaultCurrencies.add(setDefaultCurrency(this, THIRD.position))
                    }
                    getCurrency("USD_GBP").run {
                        defaultCurrencies.add(setDefaultCurrency(this, FOURTH.position))
                    }
                } else {
                    localCurrency.run {
                        defaultCurrencies.add(setDefaultCurrency(this, SECOND.position))
                    }
                }
                upsertCurrencies(defaultCurrencies)
            }
        }
    }

    private fun setDefaultCurrency(currency: Currency, order: Int): Currency {
        currency.order = order
        currency.isSelected = true
        return currency
    }
}