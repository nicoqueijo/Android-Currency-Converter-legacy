package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.animation.LayoutTransition
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.jmedeisis.draglinearlayout.DragLinearLayout
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.Order.INVALID
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.copyToClipboard
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.elementAfter
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.elementBefore
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.hasOnlyOneElement
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.hide
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isNotLastElement
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isValid
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.show
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.vibrate
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.ActiveCurrenciesViewModel

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
        initViewsAndAdapter(view)
        observeObservables()
        /*populateDefaultCurrencies()*/
        return view
    }

    private fun initViewsAndAdapter(view: View) {
        emptyList = view.findViewById(R.id.empty_list)
        scrollView = view.findViewById(R.id.scroll_view)
        dragLinearLayout = view.findViewById(R.id.drag_linear_layout)
        dragLinearLayout.setContainerScrollView(scrollView)
        dragLinearLayout.setOnViewSwapListener { _, startPosition, _, endPosition ->
            swapCurrencies(startPosition, endPosition)
        }
        keyboard = view.findViewById(R.id.keyboard)
        initFloatingActionButton(view)
        if (viewModel.wasListConstructed) {
            restoreActiveCurrencies()
        }
    }

    private fun observeObservables() {
        viewModel.databaseActiveCurrencies.observe(viewLifecycleOwner, Observer { databaseActiveCurrencies ->
            if (!viewModel.wasListConstructed) {
                constructActiveCurrencies(databaseActiveCurrencies)
            }
            if (wasCurrencyAddedViaFab(databaseActiveCurrencies)) {
                val addedCurrency = databaseActiveCurrencies.takeLast(1).single()
                viewModel.memoryActiveCurrencies.add(addedCurrency)
                addRow(addedCurrency)
            }
            dragLinearLayout.forEachIndexed { i, it ->
                styleIfFocused(viewModel.memoryActiveCurrencies[i], it as RowActiveCurrency)
            }
            toggleEmptyListViewVisibility()
        })
        /**
         * When the focused currency changes update the hints
         */
        viewModel.focusedCurrency.observe(viewLifecycleOwner, Observer { focusedCurrency ->
            updateHints(focusedCurrency)
        })
    }

    private fun updateHints(focusedCurrency: Currency?) {
        /*focusedCurrency?.let {
            focusedCurrency.conversion.conversionHint = "1"
            recyclerView.post {
                viewModel.adapterActiveCurrencies
                        .filter { it != focusedCurrency }
                        .forEach {
                            val fromRate = focusedCurrency.exchangeRate
                            val toRate = it.exchangeRate
                            val conversionValue = CurrencyConversion.convertCurrency(BigDecimal("1"),
                                    fromRate, toRate).roundToFourDecimalPlaces()
                            it.conversion.conversionHint = conversionValue.toString()
                            adapter.notifyItemChanged(viewModel.adapterActiveCurrencies.indexOf(it))
                        }
            }
            recyclerView.smoothScrollToPosition(viewModel.adapterActiveCurrencies.indexOf(focusedCurrency))
        }*/
    }

    /**
     * On the drag events, adjacent currencies need to swap position indices and this needs to be
     * reflected in memory and in the database.
     */
    private fun swapCurrencies(firstPosition: Int, secondPosition: Int) {
        viewModel.memoryActiveCurrencies[firstPosition].order = viewModel.memoryActiveCurrencies[secondPosition].order.also {
            viewModel.memoryActiveCurrencies[secondPosition].order = viewModel.memoryActiveCurrencies[firstPosition].order
        }
        viewModel.memoryActiveCurrencies[firstPosition] = viewModel.memoryActiveCurrencies[secondPosition].also {
            viewModel.memoryActiveCurrencies[secondPosition] = viewModel.memoryActiveCurrencies[firstPosition]
        }
        viewModel.upsertCurrency(viewModel.memoryActiveCurrencies[firstPosition])
        viewModel.upsertCurrency(viewModel.memoryActiveCurrencies[secondPosition])
    }

    private fun restoreActiveCurrencies() {
        viewModel.memoryActiveCurrencies.forEach { currency ->
            addRow(currency)
        }
    }

    /**
     * This indicates the user added a currency by selecting it from the SelectableCurrenciesFragment
     * that was initiated by the press of the FloatingActionButton.
     * The indication is triggered when, the only difference between [databaseActiveCurrencies] and
     * [memoryActiveCurrencies] is that [databaseActiveCurrencies] has an extra element.
     */
    private fun wasCurrencyAddedViaFab(databaseActiveCurrencies: List<Currency>) =
            (databaseActiveCurrencies.size - viewModel.memoryActiveCurrencies.size == 1 &&
                    databaseActiveCurrencies.dropLast(1) == viewModel.memoryActiveCurrencies)

    /**
     * This inflates the DragLinearLayout with the active currencies from the database when the
     * activity starts for the first time.
     */
    private fun constructActiveCurrencies(databaseActiveCurrencies: MutableList<Currency>?) {
        databaseActiveCurrencies?.forEach { currency ->
            viewModel.memoryActiveCurrencies.add(currency)
            viewModel.setFocusToFirstCurrency()
            addRow(currency)
        }
        viewModel.wasListConstructed = true
    }

    private fun styleIfFocused(currency: Currency, row: RowActiveCurrency) {
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

    private fun populateRow(currency: Currency) {

    }

    /**
     * Creates a row from a [currency] object, adds that row to the DragLinearLayout, and sets up
     * its listeners so it could be dragged, removed, and restored.
     */
    private fun addRow(currency: Currency) {
        val row = RowActiveCurrency(activity)
        row.populateRow(currency)
        dragLinearLayout.addView(row)
        dragLinearLayout.setViewDraggable(row, row)

        /**
         * Current implementation: Longclicking the [currencyCode] area "removes" the currency from the list. The currency
         *                         is hidden until the Snackbar is dismissed and at that point it is completely removed.
         *
         * Implementation to add: Actually remove this currency from the memory currencies, database currencies,
         *                        dragLinearLayout children, if focused reassign the focus.
         */
        row.currencyCode.setOnLongClickListener {

            dragLinearLayout.layoutTransition = LayoutTransition()
            dragLinearLayout.removeDragView(row)
            dragLinearLayout.layoutTransition = null

            activity?.vibrate()
            toggleEmptyListViewVisibility()

            handleRemove(dragLinearLayout.indexOfChild(row))
            dragLinearLayout.forEachIndexed { i, view ->
                styleIfFocused(viewModel.memoryActiveCurrencies[i], view as RowActiveCurrency)
            }

            Snackbar.make(dragLinearLayout, R.string.item_removed, Snackbar.LENGTH_SHORT)
                    .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        /**
                         * Current implementation: Once the Snackbar is dismissed and the user can no longer click
                         *                         undo, then we can safely remove that currency internally.
                         *
                         * Implementation to add: Nothing. Nothing should happen when the Snackbar leaves the screen.
                         *
                         */
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                                /**
                                 * Need to put this logic when the longclick is performed.
                                 * Need to put logic of re-adding the removed currency when the undo is performed.
                                 */
                                val currencyToRemove = viewModel.memoryActiveCurrencies[dragLinearLayout.indexOfChild(row)]
                                val orderOfCurrencyToRemove = currencyToRemove.order
                                shiftCurrencies(orderOfCurrencyToRemove)
                                currencyToRemove.run {
                                    isSelected = false
                                    order = INVALID.position
                                }
                                viewModel.upsertCurrency(currencyToRemove)
                                viewModel.memoryActiveCurrencies.remove(currencyToRemove)
                                dragLinearLayout.layoutTransition = LayoutTransition()
                                dragLinearLayout.removeDragView(row)
                                dragLinearLayout.layoutTransition = null
                            }


                        }
                    })
                    /**
                     * Current implementation: When the user clicks 'Undo' we restore the visibility of the currency.
                     *
                     * Implementation to add: Re-add the removed currency at the position it was previously at.
                     *                        Restore the previous state of the database currencies, memory currencies,
                     *                        dragLinearLayout children, focusedCurrency.
                     */
                    .setAction(R.string.undo) {
                        dragLinearLayout.layoutTransition = LayoutTransition()
                        row.show()
                        toggleEmptyListViewVisibility()
                        dragLinearLayout.layoutTransition = null
                    }.show()
            true
        }
        /**
         * Longpressing the [conversion] area copies its content into the clipboard.
         */
        row.conversion.setOnLongClickListener {
            activity?.copyToClipboard(row.conversion.text)
            log("Memory currencies        : ${viewModel.memoryActiveCurrencies}")
            log("Database currencies      : ${viewModel.databaseActiveCurrencies.value}")
            log("DragLinearLayout children: ${dragLinearLayout.children.asSequence()
                    .filter { it.visibility == View.VISIBLE }
                    .map { (it as RowActiveCurrency).currencyCode.text.toString() }
                    .toList()
            }")
            log("${viewModel.focusedCurrency.value}")
            true
        }
        row.conversion.setOnClickListener {
            log("conversion clicked on index: ${dragLinearLayout.indexOfChild(row)}")
            changeFocusedCurrency(dragLinearLayout.indexOfChild(row))
        }
    }

    private fun handleRemove(positionOfLongClickedCurrency: Int) {
        reassignFocusedCurrency(positionOfLongClickedCurrency)
    }

    private fun reassignFocusedCurrency(positionOfLongClickedCurrency: Int) {
        val removedCurrency = viewModel.memoryActiveCurrencies[positionOfLongClickedCurrency]
        if (viewModel.focusedCurrency.value == removedCurrency) {
            when {
                viewModel.memoryActiveCurrencies.isNotLastElement(positionOfLongClickedCurrency) -> {
                    viewModel.memoryActiveCurrencies.elementAfter(positionOfLongClickedCurrency).let { newlyFocusedCurrency ->
                        newlyFocusedCurrency.isFocused = true
                        removedCurrency.isFocused = false
                        viewModel.focusedCurrency.value = newlyFocusedCurrency
                    }
                }
                viewModel.memoryActiveCurrencies.hasOnlyOneElement() -> {
                    viewModel.focusedCurrency.value = null
                }
                else -> {
                    viewModel.memoryActiveCurrencies.elementBefore(positionOfLongClickedCurrency).let { newlyFocusedCurrency ->
                        newlyFocusedCurrency.isFocused = true
                        removedCurrency.isFocused = false
                        viewModel.focusedCurrency.value = newlyFocusedCurrency
                    }
                }
            }
        }
    }

    private fun changeFocusedCurrency(positionOfClickedCurrency: Int) {
        val clickedCurrency = viewModel.memoryActiveCurrencies[positionOfClickedCurrency]
        val positionOfPreviouslyFocusedCurrency = viewModel.memoryActiveCurrencies.indexOf(viewModel.focusedCurrency.value)
        if (positionOfPreviouslyFocusedCurrency.isValid()) {
            viewModel.memoryActiveCurrencies[positionOfPreviouslyFocusedCurrency].isFocused = false
        }
        viewModel.focusedCurrency.value = clickedCurrency
        clickedCurrency.isFocused = true
        dragLinearLayout.forEachIndexed { i, it ->
            styleIfFocused(viewModel.memoryActiveCurrencies[i], it as RowActiveCurrency)
        }
    }

    /**
     * All the currencies below the removed currency are shifted up one position to fill the gap.
     */
    private fun shiftCurrencies(orderOfCurrencyToRemove: Int) {
        run loop@{
            viewModel.memoryActiveCurrencies
                    .reversed()
                    .forEach { currency ->
                        if (currency.order == orderOfCurrencyToRemove) {
                            return@loop
                        }
                        currency.order--
                        viewModel.upsertCurrency(currency)
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

    private fun initFloatingActionButton(view: View) {
        floatingActionButton = view.findViewById(R.id.floating_action_button)
        floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_activeCurrenciesFragment_to_selectableCurrenciesFragment)
        }
    }

    private fun populateDefaultCurrencies() {
        viewModel.populateDefaultCurrencies()
    }

    companion object {
        fun log(message: String) {
            Log.d("Nicoo", message)
        }

        val currencyCodes = listOf(
                "USD_ARS",
                "USD_BRL",
                "USD_CAD",
                "USD_DKK",
                "USD_EUR"
        )
    }
}
