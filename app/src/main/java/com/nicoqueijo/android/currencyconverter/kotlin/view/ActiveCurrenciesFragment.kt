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
        initViews(view)
        observeObservables()
        /*initDefaultCurrencies()*/
        return view
    }

    private fun initViews(view: View) {
        emptyList = view.findViewById(R.id.empty_list)
        scrollView = view.findViewById(R.id.scroll_view)
        keyboard = view.findViewById(R.id.keyboard)
        initDragLinearLayout(view)
        initFloatingActionButton(view)
        if (viewModel.wasListConstructed) {
            restoreActiveCurrencies()
        }
    }

    private fun initDragLinearLayout(view: View) {
        dragLinearLayout = view.findViewById(R.id.drag_linear_layout)
        dragLinearLayout.setContainerScrollView(scrollView)
        dragLinearLayout.setOnViewSwapListener { _, startPosition, _, endPosition ->
            viewModel.swapCurrencies(startPosition, endPosition)
        }
    }

    private fun initFloatingActionButton(view: View) {
        floatingActionButton = view.findViewById(R.id.floating_action_button)
        floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_activeCurrenciesFragment_to_selectableCurrenciesFragment)
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
        if (!viewModel.wasListConstructed) {
            constructActiveCurrencies(databaseActiveCurrencies)
        }
        if (viewModel.wasCurrencyAddedViaFab(databaseActiveCurrencies)) {
            val addedCurrency = databaseActiveCurrencies.takeLast(1).single()
            viewModel.memoryActiveCurrencies.add(addedCurrency)
            addRow(addedCurrency)
        }
        viewModel.setDefaultFocus()
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
        val row = RowActiveCurrency(activity)
        row.initRow(currency)
        dragLinearLayout.addView(row)
        dragLinearLayout.setViewDraggable(row, row)
        /**
         * Removes this Currency and adjusts the state accordingly.
         */
        row.currencyCode.setOnLongClickListener {
            val currencyToRemove = viewModel.memoryActiveCurrencies[dragLinearLayout.indexOfChild(row)]
            val positionOfCurrencyToRemove = currencyToRemove.order
            viewModel.handleRemove(currencyToRemove, positionOfCurrencyToRemove)
            activity?.vibrate()
            dragLinearLayout.layoutTransition = LayoutTransition()
            dragLinearLayout.removeDragView(row)
            dragLinearLayout.layoutTransition = null
            styleRows()
            toggleEmptyListViewVisibility()
            /**
             * Re-adds the removed Currency and restores the state before the Currency was removed.
             */
            Snackbar.make(dragLinearLayout, R.string.item_removed, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.undo) {
                        dragLinearLayout.layoutTransition = LayoutTransition()
                        dragLinearLayout.addDragView(row, row, positionOfCurrencyToRemove)
                        dragLinearLayout.layoutTransition = null
                        viewModel.handleUndo(currencyToRemove, positionOfCurrencyToRemove)
                        toggleEmptyListViewVisibility()
                    }.show()
            true
        }
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

    private fun initDefaultCurrencies() {
        viewModel.initDefaultCurrencies()
    }

    companion object {
        fun log(message: String) {
            Log.d("Nicoo", message)
        }
    }
}
