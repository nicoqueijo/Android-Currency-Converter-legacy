package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.animation.LayoutTransition
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.core.view.forEach
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initViewsAndAdapter(view: View) {
        val emptyListView = view.findViewById<View>(R.id.empty_list) // connect this with the DragLinearLayout some way
        dragLinearLayout = view.findViewById(R.id.drag_linear_layout)
        scrollView = view.findViewById(R.id.scroll_view)

        dragLinearLayout.setContainerScrollView(scrollView)

        currencyCodes.forEachIndexed { i, it ->
            val row = RowActiveCurrency(activity!!)
            row.currencyCode.text = it.substring(4)
            row.flag.setImageResource(Utils.getDrawableResourceByName(it.toLowerCase(), activity))
            row.conversion.text = i.plus(1).toString()
            dragLinearLayout.addView(row)
            dragLinearLayout.setViewDraggable(row, row)
        }


        dragLinearLayout.forEach { row ->
            row as RowActiveCurrency
            row.fadingEdgeLayout.setOnLongClickListener {
                dragLinearLayout.layoutTransition = LayoutTransition()
                dragLinearLayout.removeDragView(row)
                dragLinearLayout.layoutTransition = null
                true
            }
        }

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

    companion object {
        val currencyCodes = listOf(
                "USD_AED",
                "USD_AFN",
                "USD_ALL",
                "USD_AMD",
                "USD_ANG",
                "USD_AOA",
                "USD_ARS",
                "USD_AUD",
                "USD_AWG",
                "USD_AZN",
                "USD_BAM",
                "USD_BBD",
                "USD_BDT",
                "USD_BGN",
                "USD_BHD",
                "USD_BIF",
                "USD_BMD",
                "USD_BND",
                "USD_BOB",
                "USD_BRL",
                "USD_BSD",
                "USD_BTC",
                "USD_BTN",
                "USD_BWP",
                "USD_BYN",
                "USD_BZD",
                "USD_CAD",
                "USD_CDF",
                "USD_CHF",
                "USD_CLF",
                "USD_CLP",
                "USD_CNY",
                "USD_COP",
                "USD_CRC",
                "USD_CUP",
                "USD_CVE",
                "USD_CZK"
        )
    }
}
