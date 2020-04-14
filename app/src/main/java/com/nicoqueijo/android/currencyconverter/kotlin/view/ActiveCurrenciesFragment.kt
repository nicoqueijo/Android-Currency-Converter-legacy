package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jmedeisis.draglinearlayout.DragLinearLayout
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.ActiveCurrenciesViewModel


class ActiveCurrenciesFragment : Fragment() {

    private lateinit var viewModel: ActiveCurrenciesViewModel

    private lateinit var dragLinearLayout: DragLinearLayout
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var keyboard: DecimalNumberKeyboard

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
        val emptyListView = view.findViewById<View>(R.id.empty_list) // connect this with the DragLinearLayout some way
        dragLinearLayout = view.findViewById(R.id.drag_linear_layout)
        dragLinearLayout.forEach {
            dragLinearLayout.setViewDraggable(it, it)
        }
        val row0: RowActiveCurrency = dragLinearLayout[0] as RowActiveCurrency
        val row1: RowActiveCurrency = dragLinearLayout[1] as RowActiveCurrency
        val row2: RowActiveCurrency = dragLinearLayout[2] as RowActiveCurrency

        row0.flag.setImageResource(Utils.getDrawableResourceByName("usd_ars", activity))
        row1.flag.setImageResource(Utils.getDrawableResourceByName("usd_brl", activity))
        row2.flag.setImageResource(Utils.getDrawableResourceByName("usd_clp", activity))

        row0.currencyCode.text = "ARS"
        row1.currencyCode.text = "BRL"
        row2.currencyCode.text = "CLP"

        keyboard = view.findViewById(R.id.keyboard)
        initFloatingActionButton(view)
    }

    private fun observeObservables() {
        // When currency is added or swiped, add/remove an item from DragLinearLayout
        // When the focused currency changes update the hints
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
}
