package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.animation.LayoutTransition
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
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
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.Order.INVALID
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.copyToClipboard
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.hide
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.show
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.ActiveCurrenciesViewModel

class ActiveCurrenciesFragment : Fragment() {

    private lateinit var viewModel: ActiveCurrenciesViewModel

    private lateinit var emptyListView: LinearLayout
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
        emptyListView = view.findViewById(R.id.empty_list)
        scrollView = view.findViewById(R.id.scroll_view)
        dragLinearLayout = view.findViewById(R.id.drag_linear_layout)
        dragLinearLayout.setContainerScrollView(scrollView)
        dragLinearLayout.setOnViewSwapListener { _, firstPosition, _, secondPosition ->
            swapCurrencies(firstPosition, secondPosition)
        }
        keyboard = view.findViewById(R.id.keyboard)
        initFloatingActionButton(view)
        if (viewModel.wasListConstructed) {
            restoreActiveCurrencies()
        }
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

    private fun observeObservables() {
        viewModel.databaseActiveCurrencies.observe(viewLifecycleOwner, Observer { databaseActiveCurrencies ->
            toggleEmptyListViewVisibility(databaseActiveCurrencies)
            if (!viewModel.wasListConstructed) {
                constructActiveCurrencies(databaseActiveCurrencies)
            }
            if (wasCurrencyAddedViaFab(databaseActiveCurrencies)) {
                val addedCurrency = databaseActiveCurrencies.takeLast(1).single()
                viewModel.memoryActiveCurrencies.add(addedCurrency)
                addRow(addedCurrency)
            }
        })
        /**
         * When the focused currency changes update the hints
         */
        viewModel.focusedCurrency.observe(viewLifecycleOwner, Observer { focusedCurrency ->
            /*updateHints(focusedCurrency)*/
        })
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
            addRow(currency)
        }
        viewModel.wasListConstructed = true
    }

    /**
     * Creates a row from a [currency] object, adds that row to the DragLinearLayout, and sets up
     * its listeners so it could be dragged, removed, and restored.
     */
    private fun addRow(currency: Currency) {
        RowActiveCurrency(activity).run row@{
            currencyCode.text = currency.trimmedCurrencyCode
            flag.setImageResource(Utils.getDrawableResourceByName(currency.currencyCode.toLowerCase(), activity))
            conversion.text = "100.00" // Remove this later (is just for testing)
            dragLinearLayout.run {
                addView(this@row)
                setViewDraggable(this@row, this@row)
            }
            /**
             * Longpressing the [currencyCode] area removes the currency from the list. The currency
             * is hidden until the Snackbar is dismissed and at that point it's completely removed.
             */
            this.currencyCode.setOnLongClickListener {
                dragLinearLayout.run {
                    layoutTransition = LayoutTransition()
                    this@row.hide()
                    layoutTransition = null
                    Snackbar.make(this, R.string.item_removed, Snackbar.LENGTH_SHORT)
                            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                /**
                                 * Once the Snackbar is dismissed and the user can no longer click
                                 * undo, then we can safely remove that currency internally.
                                 */
                                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                    super.onDismissed(transientBottomBar, event)
                                    if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                                        val currencyToRemove = viewModel.memoryActiveCurrencies[dragLinearLayout.indexOfChild(this@row)]
                                        val orderOfCurrencyToRemove = currencyToRemove.order
                                        shiftCurrencies(orderOfCurrencyToRemove)
                                        currencyToRemove.run {
                                            isSelected = false
                                            order = INVALID.position
                                        }
                                        viewModel.run {
                                            upsertCurrency(currencyToRemove)
                                            memoryActiveCurrencies.remove(currencyToRemove)
                                        }
                                        layoutTransition = LayoutTransition()
                                        removeDragView(this@row)
                                        layoutTransition = null
                                    }
                                }
                            })
                            /**
                             * When the user clicks 'Undo' we restore the visibility of the currency.
                             */
                            .setAction(R.string.undo) {
                                layoutTransition = LayoutTransition()
                                this@row.show()
                                layoutTransition = null
                            }.show()
                }
                true
            }
            /**
             * Longpressing the [conversion] area copies its content into the clipboard.
             */
            this.conversion.setOnLongClickListener {
                val conversionText = conversion.text.toString()
                activity?.copyToClipboard(conversionText)
                true
            }
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

    private fun toggleEmptyListViewVisibility(currencies: MutableList<Currency>) {
        when {
            currencies.isEmpty() -> emptyListView.show()
            currencies.isNotEmpty() -> emptyListView.hide()
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
