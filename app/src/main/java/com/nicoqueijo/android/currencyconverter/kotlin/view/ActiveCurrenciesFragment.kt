package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.jmedeisis.draglinearlayout.DragLinearLayout
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.CurrencyConversion
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.copyToClipboard
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.hasOnlyOneElement
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.hide
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isViewVisible
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.roundToFourDecimalPlaces
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.show
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.vibrate
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.ActiveCurrenciesViewModel
import java.math.BigDecimal

class ActiveCurrenciesFragment : Fragment() {

    private lateinit var viewModel: ActiveCurrenciesViewModel

    private lateinit var emptyList: LinearLayout
    private lateinit var dragLinearLayout: DragLinearLayout
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var keyboard: DecimalNumberKeyboard
    private lateinit var scrollView: ScrollView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_active_currencies, container, false)
        viewModel = ViewModelProvider(this).get(ActiveCurrenciesViewModel::class.java)
        initViews(view)
        observeObservables()
        /*viewModel.initDefaultCurrencies()*/
        return view
    }

    private fun initViews(view: View) {
        emptyList = view.findViewById(R.id.empty_list)
        scrollView = view.findViewById(R.id.scroll_view)
        keyboard = view.findViewById(R.id.keyboard)
        initDragLinearLayout(view)
        initFloatingActionButton(view)
        initKeyboardListener()
        if (viewModel.wasListConstructed) {
            restoreActiveCurrencies()
        }
    }

    private fun initDragLinearLayout(view: View) {
        dragLinearLayout = view.findViewById<DragLinearLayout>(R.id.drag_linear_layout).apply {
            setContainerScrollView(scrollView)
            setOnViewSwapListener { _, startPosition, _, endPosition ->
                viewModel.swapCurrencies(startPosition, endPosition)
            }
        }
    }

    private fun initFloatingActionButton(view: View) {
        floatingActionButton = view.findViewById<FloatingActionButton>(R.id.floating_action_button).apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_activeCurrenciesFragment_to_selectableCurrenciesFragment)
            }
        }
    }

    /**
     * If the [button] is a Button we know that belongs to chars 0-9 or the decimal
     * separator as those were declared as Buttons.
     * If the [button] is an ImageButton that can only mean that it is the backspace
     * key as that was the only one declared as an ImageButton.
     *
     * On each key click event, we want to validate the input against what already is in the
     * TextView. If it is valid we want to run the conversion of that value against all other
     * currencies and update the TextView of all other currencies.
     */
    private fun initKeyboardListener() {
        keyboard.onKeyClickedListener { button ->
            scrollToFocusedCurrency()
            processKeyboardInput(button)
        }
        keyboard.onKeyLongClickedListener {
            scrollToFocusedCurrency()
            clearConversions()
        }
    }

    private fun processKeyboardInput(button: View?) {
        var existingText = viewModel.focusedCurrency.value?.conversion?.conversionString
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
                viewModel.focusedCurrency.value?.conversion?.conversionString = existingText!!
            }
        }
        if (isInputValid) {
            runConversions()
        } else {
            vibrateAndShake()
        }
    }

    private fun clearConversions() {
        viewModel.focusedCurrency.value?.conversion?.conversionString = ""
        viewModel.memoryActiveCurrencies
                .filter { it != viewModel.focusedCurrency.value }
                .forEach {
                    it.conversion.conversionString = ""
                }
        updateRows()
    }

    private fun runConversions() {
        val focusedCurrency = viewModel.focusedCurrency.value
        viewModel.memoryActiveCurrencies
                .filter { it != focusedCurrency }
                .forEach {
                    val fromRate = focusedCurrency!!.exchangeRate
                    val toRate = it.exchangeRate
                    if (focusedCurrency.conversion.conversionString.isNotEmpty()) {
                        val conversionValue = CurrencyConversion.convertCurrency(BigDecimal(focusedCurrency
                                .conversion.conversionString.replace(",", ".")), fromRate, toRate)
                        it.conversion.conversionValue = conversionValue
                    } else {
                        it.conversion.conversionString = ""
                    }
                }
        updateRows()
    }

    private fun updateRows() {
        dragLinearLayout.children
                .forEachIndexed { i, row ->
                    row as RowActiveCurrency
                    row.conversion.text = viewModel.memoryActiveCurrencies[i].conversion.conversionText
                }
    }

    private fun vibrateAndShake() {
        keyboard.context.vibrate()
        (dragLinearLayout[viewModel.memoryActiveCurrencies
                .indexOf(viewModel.focusedCurrency.value)] as RowActiveCurrency)
                .conversion.startAnimation(AnimationUtils.loadAnimation(viewModel.getApplication(),
                        R.anim.shake))
    }

    private fun cleanInput(input: String): String {
        return when (input) {
            viewModel.decimalSeparator -> "0${viewModel.decimalSeparator}"
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
        if (!input.contains(viewModel.decimalSeparator) && input.length > maxDigitsAllowed) {
            viewModel.focusedCurrency.value?.conversion?.conversionString = input.dropLast(1)
            return false
        }
        viewModel.focusedCurrency.value?.conversion?.conversionString = input
        return true
    }

    private fun validateDecimalPlaces(input: String): Boolean {
        val maxDecimalPlacesAllowed = 4
        if (input.contains(viewModel.decimalSeparator) &&
                input.substring(input.indexOf(viewModel.decimalSeparator) + 1).length > maxDecimalPlacesAllowed) {
            viewModel.focusedCurrency.value?.conversion?.conversionString = input.dropLast(1)
            return false
        }
        viewModel.focusedCurrency.value?.conversion?.conversionString = input
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun validateDecimalSeparator(input: String): Boolean {
        val decimalSeparatorCount = input.asSequence()
                .count { char ->
                    char.toString() == viewModel.decimalSeparator
                }
        if (decimalSeparatorCount > 1) {
            viewModel.focusedCurrency.value?.conversion?.conversionString = input.dropLast(1)
            return false
        }
        viewModel.focusedCurrency.value?.conversion?.conversionString = input
        return true
    }

    private fun validateZeros(input: String): Boolean {
        if (input.length == 2) {
            if (input[0] == '0' && input[1] != viewModel.decimalSeparator.single()) {
                viewModel.focusedCurrency.value?.conversion?.conversionString = input[1].toString()
                return true
            }
        }
        viewModel.focusedCurrency.value?.conversion?.conversionString = input
        return true
    }

    private fun scrollToFocusedCurrency() {
        val focusedCurrency = viewModel.focusedCurrency.value
        focusedCurrency?.let {
            val focusedCurrencyRow = dragLinearLayout.getChildAt(viewModel.memoryActiveCurrencies
                    .indexOf(focusedCurrency))
            if (!scrollView.isViewVisible(focusedCurrencyRow)) {
                scrollView.smoothScrollTo(0, focusedCurrencyRow.top)
            }
        }
    }

    private fun restoreActiveCurrencies() {
        viewModel.memoryActiveCurrencies.forEach { currency ->
            addRow(currency)
        }
    }

    private fun observeObservables() {
        viewModel.databaseActiveCurrencies.observe(viewLifecycleOwner, Observer { databaseActiveCurrencies ->
            initActiveCurrencies(databaseActiveCurrencies)
            styleRows()
            toggleEmptyListViewVisibility()
        })
        /**
         * When the focused currency changes update the hints
         */
        viewModel.focusedCurrency.observe(viewLifecycleOwner, Observer { focusedCurrency ->
            updateHints(focusedCurrency)
        })
    }

    /**
     * Determines how it should inflate the list of currencies when the database storing the state
     * of the currencies emits updates.
     */
    private fun initActiveCurrencies(databaseActiveCurrencies: List<Currency>) {
        viewModel.run {
            if (!wasListConstructed) {
                constructActiveCurrencies(databaseActiveCurrencies)
            }
            if (wasCurrencyAddedViaFab(databaseActiveCurrencies)) {
                databaseActiveCurrencies.takeLast(1).single().let {
                    memoryActiveCurrencies.add(it)
                    addRow(it)
                    if (!memoryActiveCurrencies.hasOnlyOneElement()) {
                        runConversions()
                        scrollToFocusedCurrency()
                    }
                }
                updateHints(viewModel.focusedCurrency.value)
            }
            setDefaultFocus()
        }
    }

    private fun updateHints(focusedCurrency: Currency?) {
        focusedCurrency?.let {
            focusedCurrency.conversion.conversionHint = "1"
            viewModel.memoryActiveCurrencies
                    .filter { it != focusedCurrency }
                    .forEach {
                        val fromRate = focusedCurrency.exchangeRate
                        val toRate = it.exchangeRate
                        val conversionValue = CurrencyConversion.convertCurrency(BigDecimal("1"),
                                fromRate, toRate).roundToFourDecimalPlaces().toString()
                        it.conversion.conversionHint = conversionValue
                    }
            dragLinearLayout.children
                    .forEachIndexed { i, row ->
                        row as RowActiveCurrency
                        row.conversion.hint = viewModel.memoryActiveCurrencies[i].conversion.conversionHint
                    }
        }
    }

    /**
     * This inflates the DragLinearLayout with the active currencies from the database when the
     * activity starts for the first time.
     */
    private fun constructActiveCurrencies(databaseActiveCurrencies: List<Currency>) {
        databaseActiveCurrencies.forEach { currency ->
            viewModel.memoryActiveCurrencies.add(currency)
            addRow(currency)
        }
        viewModel.wasListConstructed = true
    }

    private fun styleRows() {
        dragLinearLayout.forEachIndexed { i, row ->
            styleRow(viewModel.memoryActiveCurrencies[i], row as RowActiveCurrency)
        }
    }

    /**
     * Styles the row in accordance to the focus state of its Currency. A row containing a focused
     * Currency should have blinking cursor at the end of it's conversion field and a dark gray background.
     */
    private fun styleRow(currency: Currency, row: RowActiveCurrency) {
        row.run {
            if (currency.isFocused) {
                rowCanvas.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_gray))
                blinkingCursor.startAnimation(AnimationUtils.loadAnimation(viewModel.getApplication(), R.anim.blink))
            } else {
                rowCanvas.background = ContextCompat.getDrawable(context, R.drawable.background_row_active_currency)
                blinkingCursor.clearAnimation()
            }
        }
    }

    /**
     * Creates a row from a [currency], adds that row to the DragLinearLayout, and sets up
     * its listeners so it could be dragged, removed, and restored.
     */
    private fun addRow(currency: Currency) {
        RowActiveCurrency(activity).run row@{
            initRow(currency)
            dragLinearLayout.run {
                addView(this@row)
                setViewDraggable(this@row, this@row)
                /**
                 * Removes this Currency and adjusts the state accordingly.
                 */
                currencyCode.setOnLongClickListener {
                    val currencyToRemove = viewModel.memoryActiveCurrencies[indexOfChild(this@row)]
                    val positionOfCurrencyToRemove = currencyToRemove.order
                    viewModel.handleRemove(currencyToRemove, positionOfCurrencyToRemove)
                    activity?.vibrate()
                    layoutTransition = LayoutTransition()
                    removeDragView(this@row)
                    layoutTransition = null
                    styleRows()
                    toggleEmptyListViewVisibility()
                    /**
                     * Re-adds the removed Currency and restores the state before the Currency was removed.
                     */
                    Snackbar.make(this, R.string.item_removed, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.undo) {
                                layoutTransition = LayoutTransition()
                                addDragView(this@row, this@row, positionOfCurrencyToRemove)
                                layoutTransition = null
                                viewModel.handleUndo(currencyToRemove, positionOfCurrencyToRemove)
                                toggleEmptyListViewVisibility()
                            }.show()
                    true
                }
                conversion.setOnLongClickListener {
                    activity?.copyToClipboard(this@row.conversion.text)
                    true
                }
                conversion.setOnClickListener {
                    viewModel.changeFocusedCurrency(indexOfChild(this@row))
                    styleRows()
                }
            }
        }
    }

    private fun toggleEmptyListViewVisibility() {
        val numOfVisibleRows = dragLinearLayout.children.asSequence()
                .filter { it.isVisible }
                .count()
        when (numOfVisibleRows) {
            0 -> emptyList.show()
            else -> emptyList.hide()
        }
    }
}
