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
        dragLinearLayout = view.findViewById(R.id.drag_linear_layout)
        scrollView = view.findViewById(R.id.scroll_view)
        dragLinearLayout.setContainerScrollView(scrollView)
        dragLinearLayout.setOnViewSwapListener { _, firstPosition, _, secondPosition ->
            viewModel.memoryActiveCurrencies[firstPosition].order = viewModel.memoryActiveCurrencies[secondPosition].order.also {
                viewModel.memoryActiveCurrencies[secondPosition].order = viewModel.memoryActiveCurrencies[firstPosition].order
            }
            viewModel.memoryActiveCurrencies[firstPosition] = viewModel.memoryActiveCurrencies[secondPosition].also {
                viewModel.memoryActiveCurrencies[secondPosition] = viewModel.memoryActiveCurrencies[firstPosition]
            }
            viewModel.upsertCurrency(viewModel.memoryActiveCurrencies[firstPosition])
            viewModel.upsertCurrency(viewModel.memoryActiveCurrencies[secondPosition])
        }
        keyboard = view.findViewById(R.id.keyboard)
        initFloatingActionButton(view)
        if (viewModel.wasListConstructed) {
            restoreActiveCurrencies()
        }
    }

    private fun restoreActiveCurrencies() {
        viewModel.memoryActiveCurrencies.forEach { currency ->
            addRow(currency)
        }
    }

    private fun observeObservables() {
        viewModel.databaseActiveCurrencies.observe(viewLifecycleOwner, Observer { databaseActiveCurrencies ->
            toggleKeyboardVisibility(databaseActiveCurrencies)
            if (!viewModel.wasListConstructed) {
                constructActiveCurrencies(databaseActiveCurrencies)
            }
            if (wasCurrencyAddedViaFab(databaseActiveCurrencies)) {
                log("Currency was added via FAB")
                val addedCurrency = databaseActiveCurrencies.takeLast(1).single()
                viewModel.memoryActiveCurrencies.add(addedCurrency)
                addRow(addedCurrency)
            }
            if (databaseActiveCurrencies.isEmpty()) {
                emptyListView.show()
            } else {
                emptyListView.hide()
            }
            // When currency is added or swiped, add/remove an item from DragLinearLayout
        })
        // When the focused currency changes update the hints
        viewModel.focusedCurrency.observe(viewLifecycleOwner, Observer { focusedCurrency ->
            /*updateHints(focusedCurrency)*/
        })
    }

    /**
     * This indicates the user added a currency by selecting it from the SelectableCurrenciesFragment
     * that was initiated by the press of the FloatingActionButton.
     * The indication is triggered when, the only difference between [dbActiveCurrencies] and
     * [memoryActiveCurrencies] is that [dbActiveCurrencies] has one extra element.
     */
    private fun wasCurrencyAddedViaFab(dbActiveCurrencies: List<Currency>) =
            (dbActiveCurrencies.size - viewModel.memoryActiveCurrencies.size == 1 &&
                    dbActiveCurrencies.dropLast(1) == viewModel.memoryActiveCurrencies)

    /**
     *
     */
    private fun constructActiveCurrencies(dbActiveCurrencies: MutableList<Currency>?) {
        dbActiveCurrencies?.forEach { currency ->
            viewModel.memoryActiveCurrencies.add(currency)
            addRow(currency)
        }
        viewModel.wasListConstructed = true
    }

    /**
     * Creates a row from a Currency object, adds that row to the DragLinearLayout, and sets up
     * its listeners.
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
            // Remove item
            this.currencyCode.setOnLongClickListener {
                dragLinearLayout.run {

                    log("Hide row ${(this@row).currencyCode.text}")
                    layoutTransition = LayoutTransition()
                    this@row.visibility = View.GONE
                    layoutTransition = null

                    Snackbar.make(this, R.string.item_removed, Snackbar.LENGTH_LONG)
                            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                    super.onDismissed(transientBottomBar, event)
                                    if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                                        log("Actually remove the ${(this@row).currencyCode.text} currency/row here")
                                        // Remove the row from DragLinearLayout
                                        // Remove item from memory
                                        // Remove item from database

                                        val currencyToRemove = viewModel.memoryActiveCurrencies[dragLinearLayout.indexOfChild(this@row)]
                                        val orderOfCurrencyToRemove = currencyToRemove.order
                                        shiftCurrenciesUp(orderOfCurrencyToRemove)
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
                            }).setAction(R.string.undo) {
                                log("Show row ${(this@row).currencyCode.text}")

                                layoutTransition = LayoutTransition()
                                this@row.visibility = View.VISIBLE
                                layoutTransition = null


                                /*currencyToRemove.run {
                                    isSelected = true
                                    order = orderOfCurrencyToRemove
                                }
                                *//*log("Currency to remove: $currencyToRemove")*//*
                                viewModel.upsertCurrency(currencyToRemove)
                                for (i in orderOfCurrencyToRemove until viewModel.memoryActiveCurrencies.size) {
                                    val activeCurrency = viewModel.memoryActiveCurrencies[i]
                                    activeCurrency.order++
                                    viewModel.upsertCurrency(activeCurrency)
                                }
                                viewModel.memoryActiveCurrencies.add(orderOfCurrencyToRemove, currencyToRemove)
                                addView(this@row, orderOfCurrencyToRemove)
                                setViewDraggable(this@row, this@row)
                                *//*log("Memory   currencies: ${viewModel.memoryActiveCurrencies}")
                                log("Database currencies: ${viewModel.databaseActiveCurrencies.value}")*/


                            }.show()
                }
                true
            }
            // Copy conversion to clipboard & display toast
            this.conversion.setOnLongClickListener {
                log("Memory   currencies: ${viewModel.memoryActiveCurrencies}")
                log("Database currencies: ${viewModel.databaseActiveCurrencies.value}")
                val conversionText = conversion.text.toString()
                activity?.copyToClipboard(conversionText)
                true
            }
        }
    }

    private fun shiftCurrenciesDown() {

    }

    private fun shiftCurrenciesUp(orderOfCurrencyToRemove: Int) {
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

    private fun toggleKeyboardVisibility(currencies: MutableList<Currency>) {
        when {
            currencies.isEmpty() -> {
                // Drop down keyboard
            }
            currencies.isNotEmpty() -> {
                // Pop up keyboard
            }
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
