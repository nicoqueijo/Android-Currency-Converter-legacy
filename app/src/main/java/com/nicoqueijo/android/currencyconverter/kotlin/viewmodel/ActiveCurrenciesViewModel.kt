package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import android.widget.Button
import android.widget.ImageButton
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
import java.text.DecimalFormat
import java.text.NumberFormat
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

    private var decimalFormatter: DecimalFormat
    private var decimalSeparator: String
    private var groupingSeparator: String

    init {
        val numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault())
        val conversionPattern = "###,##0.0000"
        decimalFormatter = numberFormatter as DecimalFormat
        decimalFormatter.applyPattern(conversionPattern)
        groupingSeparator = decimalFormatter.decimalFormatSymbols.groupingSeparator.toString()
        decimalSeparator = decimalFormatter.decimalFormatSymbols.decimalSeparator.toString()
    }

    fun handleKeyPressed(button: View?): Boolean {
        triggerScrollToPosition()
        when (button) {
            is Button -> {
                val existingText = focusedCurrency.value?.conversion?.conversionText
                val keyPressed = button.text
                var input = existingText + keyPressed
                input = cleanInput(input)
                if (!isInputValid(input)) {
                    focusedCurrency.value?.conversion?.hasInvalidInput = true
                    return false
                }
                return true
            }
            is ImageButton -> {
                var existingText = focusedCurrency.value?.conversion?.conversionText
                existingText = existingText?.dropLast(1)
                focusedCurrency.value?.conversion?.conversionText = existingText!!
                return true
            }
            else -> return false // never reached but kotlin requires it for exhaustiveness
        }
    }

    fun handleKeyLongPressed() {
        triggerScrollToPosition()
        focusedCurrency.value?.conversion?.conversionText = ""
    }

    private fun cleanInput(input: String): String {
        return when (input) {
            "." -> "0$decimalSeparator"
            "00" -> "0"
            else -> input
        }
    }

    private fun isInputValid(input: String): Boolean {
        return validateLength(input) && validateDecimalPlaces(input) &&
                validateDecimalSeparator(input) && validateZeros(input)
    }

    private fun validateLength(input: String): Boolean {
        val maxDigitsAllowed = 20
        if (!input.contains(decimalSeparator) && input.length > maxDigitsAllowed) {
            focusedCurrency.value?.conversion?.conversionText = input.dropLast(1)
            return false
        }
        focusedCurrency.value?.conversion?.conversionText = input
        return true
    }

    private fun validateDecimalPlaces(input: String): Boolean {
        val maxDecimalPlacesAllowed = 4
        if (input.contains(decimalSeparator) &&
                input.substring(input.indexOf(decimalSeparator) + 1).length > maxDecimalPlacesAllowed) {
            focusedCurrency.value?.conversion?.conversionText = input.dropLast(1)
            return false
        }
        focusedCurrency.value?.conversion?.conversionText = input
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun validateDecimalSeparator(input: String): Boolean {
        val decimalSeparatorCount = input.asSequence()
                .count { char ->
                    char.toString() == decimalSeparator
                }
        if (decimalSeparatorCount > 1) {
            focusedCurrency.value?.conversion?.conversionText = input.dropLast(1)
            return false
        }
        focusedCurrency.value?.conversion?.conversionText = input
        return true
    }

    private fun validateZeros(input: String): Boolean {
        if (input.length == 2) {
            if (input[0] == '0' && input[1] != decimalSeparator.single()) {
                focusedCurrency.value?.conversion?.conversionText = input[1].toString()
                return true
            }
        }
        focusedCurrency.value?.conversion?.conversionText = input
        return true
    }

    fun setFocusToFirstCurrency() {
        if (focusedCurrency.value == null) {
            focusedCurrency.value = adapterActiveCurrencies.take(1)[0].also { firstCurrency ->
                firstCurrency.isFocused = true
            }
        }
    }

    fun reconcileCurrencies(currencies: MutableList<Currency>) {
        copyVolatileFields(currencies)
        setFocusedCurrency(currencies)
    }

    /**
     * [currencies] will have +/- 1 elements than [adapterActiveCurrencies] due to the swiping or
     * adding event but they will have no volatile data (conversion, isFocused).
     * Goals is to copy that volatile data stored in [adapterActiveCurrencies]
     */
    private fun copyVolatileFields(currencies: MutableList<Currency>) {
        adapterActiveCurrencies.forEach { currency ->
            if (currency.order != INVALID.position && currencies.contains(currency)) {
                currencies[currencies.indexOf(currency)] = currency
            }
        }
    }

    private fun setFocusedCurrency(currencies: MutableList<Currency>) {
        focusedCurrency.value?.let {
            focusedCurrency.value = currencies[currencies.indexOf(it)]
            currencies[currencies.indexOf(it)].isFocused = true
        }
    }

    /**
     *  The RecyclerView scrolls to the position of the focused currency when it is notified that
     *  the value of the focused currency has changed. This is a way to force that call.
     */
    private fun triggerScrollToPosition() {
        focusedCurrency.value = focusedCurrency.value
    }

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