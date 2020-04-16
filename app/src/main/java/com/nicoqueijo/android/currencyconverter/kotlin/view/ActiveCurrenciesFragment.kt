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
import com.google.android.material.snackbar.Snackbar
import com.jmedeisis.draglinearlayout.DragLinearLayout
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils
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
        populateDefaultCurrencies()
        return view
    }

    private fun initViewsAndAdapter(view: View) {
        emptyListView = view.findViewById(R.id.empty_list) // connect this with the DragLinearLayout some way
        dragLinearLayout = view.findViewById(R.id.drag_linear_layout)
        scrollView = view.findViewById(R.id.scroll_view)
        dragLinearLayout.setContainerScrollView(scrollView)
        keyboard = view.findViewById(R.id.keyboard)
        initFloatingActionButton(view)

        if (viewModel.listConstructed) {
            // If list was initialized from the first launch just redraw it from the data we have
            viewModel.activeCurrencies.value?.forEach { activeCurrency ->
                RowActiveCurrency(activity).run row@{
                    currencyCode.text = activeCurrency.trimmedCurrencyCode
                    flag.setImageResource(Utils.getDrawableResourceByName(activeCurrency.currencyCode.toLowerCase(), activity))
                    conversion.text = activeCurrency.conversion.conversionText
                    dragLinearLayout.run {
                        addView(this@row)
                        setViewDraggable(this@row, this@row)
                    }
                    this.currencyCode.setOnLongClickListener {
                        dragLinearLayout.run {
                            layoutTransition = LayoutTransition()
                            removeDragView(this)
                            layoutTransition = null
                            Snackbar.make(this, R.string.item_removed, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.undo) {
                                        // handle the undo
                                    }.show()
                        }
                        true
                    }
                }
            }
        }
    }

    private fun observeObservables() {
        viewModel.activeCurrencies.observe(viewLifecycleOwner, Observer { currencies ->
            toggleKeyboardVisibility(currencies)
            if (!viewModel.listConstructed) {
                constructList(currencies)
            }
            // When currency is added or swiped, add/remove an item from DragLinearLayout
        })

        // When the focused currency changes update the hints
        viewModel.focusedCurrency.observe(viewLifecycleOwner, Observer { focusedCurrency ->
            /*updateHints(focusedCurrency)*/
        })
    }

    /**
     *
     */
    private fun constructList(currencies: MutableList<Currency>?) {
        currencies?.forEach { currency ->
            RowActiveCurrency(activity).run row@{
                currencyCode.text = currency.trimmedCurrencyCode
                flag.setImageResource(Utils.getDrawableResourceByName(currency.currencyCode.toLowerCase(), activity))
                conversion.text = currency.conversion.conversionText
                dragLinearLayout.run {
                    addView(this@row)
                    setViewDraggable(this@row, this@row)
                }
                this.currencyCode.setOnLongClickListener {
                    dragLinearLayout.run {
                        layoutTransition = LayoutTransition()
                        removeDragView(this)
                        layoutTransition = null
                        Snackbar.make(this, R.string.item_removed, Snackbar.LENGTH_LONG)
                                .setAction(R.string.undo) {
                                    // handle the undo
                                }.show()
                    }
                    true
                }
            }
        }
        viewModel.listConstructed = true
    }

    private fun addRowAtEnd() {

    }

    private fun addRowAtIndex(/*index or currency code*/) {

    }

    private fun removeRowAtIndex(/*index or currency code*/) {

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
