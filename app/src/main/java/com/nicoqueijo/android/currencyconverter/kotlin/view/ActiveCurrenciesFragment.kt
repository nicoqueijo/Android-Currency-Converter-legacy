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
import com.google.android.material.snackbar.Snackbar
import com.jmedeisis.draglinearlayout.DragLinearLayout
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.Order.INVALID
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.copyToClipboard
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.hide
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
            viewModel.swapCurrencies(startPosition, endPosition)
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
            if (viewModel.wasCurrencyAddedViaFab(databaseActiveCurrencies)) {
                val addedCurrency = databaseActiveCurrencies.takeLast(1).single()
                viewModel.memoryActiveCurrencies.add(addedCurrency)
                addRow(addedCurrency)
            }
            viewModel.setDefaultFocus()
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

    private fun restoreActiveCurrencies() {
        viewModel.memoryActiveCurrencies.forEach { currency ->
            addRow(currency)
        }
    }

    /**
     * This inflates the DragLinearLayout with the active currencies from the database when the
     * activity starts for the first time.
     */
    private fun constructActiveCurrencies(databaseActiveCurrencies: MutableList<Currency>?) {
        databaseActiveCurrencies?.forEach { currency ->
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
        val row = RowActiveCurrency(activity)
        row.populateRow(currency)
        dragLinearLayout.addView(row)
        dragLinearLayout.setViewDraggable(row, row)

        /**
         * Implementation to add: Actually remove this currency from the memory currencies, database currencies,
         *                        dragLinearLayout children, if focused reassign the focus.
         */
        row.currencyCode.setOnLongClickListener {

            val currencyToRemove = viewModel.memoryActiveCurrencies[dragLinearLayout.indexOfChild(row)]
            val orderOfCurrencyToRemove = currencyToRemove.order
            viewModel.handleRemove(orderOfCurrencyToRemove)
            currencyToRemove.isSelected = false
            currencyToRemove.order = INVALID.position
            viewModel.upsertCurrency(currencyToRemove)
            viewModel.memoryActiveCurrencies.remove(currencyToRemove)

            activity?.vibrate()

            dragLinearLayout.layoutTransition = LayoutTransition()
            dragLinearLayout.removeDragView(row)
            dragLinearLayout.layoutTransition = null

            styleRows()

            toggleEmptyListViewVisibility()
            Snackbar.make(dragLinearLayout, R.string.item_removed, Snackbar.LENGTH_SHORT)
                    /**
                     * Implementation to add: Re-add the removed currency at the position it was previously at.
                     *                        Restore the previous state of the database currencies, memory currencies,
                     *                        dragLinearLayout children, focusedCurrency.
                     */
                    .setAction(R.string.undo) {
                        dragLinearLayout.layoutTransition = LayoutTransition()
                        dragLinearLayout.addDragView(row, row, orderOfCurrencyToRemove)
                        dragLinearLayout.layoutTransition = null

                        currencyToRemove.isSelected = true
                        currencyToRemove.order = orderOfCurrencyToRemove
                        viewModel.upsertCurrency(currencyToRemove)
                        for (i in orderOfCurrencyToRemove until viewModel.memoryActiveCurrencies.size) {
                            viewModel.memoryActiveCurrencies[i].let {
                                it.order++
                                viewModel.upsertCurrency(it)
                            }
                        }
                        viewModel.memoryActiveCurrencies.add(orderOfCurrencyToRemove, currencyToRemove)

                        toggleEmptyListViewVisibility()
                    }.show()
            true
        }
        /**
         * Longpressing the [conversion] area copies its content into the clipboard.
         */
        row.conversion.setOnLongClickListener {
            activity?.copyToClipboard(row.conversion.text)
            true
        }
        row.conversion.setOnClickListener {
            viewModel.changeFocusedCurrency(dragLinearLayout.indexOfChild(row))
            styleRows()
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
    }
}
