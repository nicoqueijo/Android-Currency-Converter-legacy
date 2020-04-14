package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
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

    private fun initViewsAndAdapter(view: View) {
        val emptyListView = view.findViewById<View>(R.id.empty_list) // connect this with the DragLinearLayout some way
        dragLinearLayout = view.findViewById(R.id.drag_linear_layout)
        scrollView = view.findViewById(R.id.scroll_view)

        dragLinearLayout.setContainerScrollView(scrollView)

        currencyCodes.forEach {
            val row = RowActiveCurrency(activity!!)
            row.currencyCode.text = it.substring(4)
            row.flag.setImageResource(Utils.getDrawableResourceByName(it.toLowerCase(), activity))
            row.conversion.text = "100.00"
            dragLinearLayout.addView(row)
            dragLinearLayout.setViewDraggable(row, row)
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
                "USD_CNH",
                "USD_CNY",
                "USD_COP",
                "USD_CRC",
                "USD_CUC",
                "USD_CUP",
                "USD_CVE",
                "USD_CZK",
                "USD_DJF",
                "USD_DKK",
                "USD_DOP",
                "USD_DZD",
                "USD_EGP",
                "USD_ERN",
                "USD_ETB",
                "USD_EUR",
                "USD_FJD",
                "USD_FKP",
                "USD_GBP",
                "USD_GEL",
                "USD_GGP",
                "USD_GHS",
                "USD_GIP",
                "USD_GMD",
                "USD_GNF",
                "USD_GTQ",
                "USD_GYD",
                "USD_HKD",
                "USD_HNL",
                "USD_HRK",
                "USD_HTG",
                "USD_HUF",
                "USD_IDR",
                "USD_ILS",
                "USD_IMP",
                "USD_INR",
                "USD_IQD",
                "USD_IRR",
                "USD_ISK",
                "USD_JEP",
                "USD_JMD",
                "USD_JOD",
                "USD_JPY",
                "USD_KES",
                "USD_KGS",
                "USD_KHR",
                "USD_KMF",
                "USD_KPW",
                "USD_KRW",
                "USD_KWD",
                "USD_KYD",
                "USD_KZT",
                "USD_LAK",
                "USD_LBP",
                "USD_LKR",
                "USD_LRD",
                "USD_LSL",
                "USD_LYD",
                "USD_MAD",
                "USD_MDL",
                "USD_MGA",
                "USD_MKD",
                "USD_MMK",
                "USD_MNT",
                "USD_MOP",
                "USD_MRO",
                "USD_MRU",
                "USD_MUR",
                "USD_MVR",
                "USD_MWK",
                "USD_MXN",
                "USD_MYR",
                "USD_MZN",
                "USD_NAD",
                "USD_NGN",
                "USD_NIO",
                "USD_NOK",
                "USD_NPR",
                "USD_NZD",
                "USD_OMR",
                "USD_PAB",
                "USD_PEN",
                "USD_PGK",
                "USD_PHP",
                "USD_PKR",
                "USD_PLN",
                "USD_PYG",
                "USD_QAR",
                "USD_RON",
                "USD_RSD",
                "USD_RUB",
                "USD_RWF",
                "USD_SAR",
                "USD_SBD",
                "USD_SCR",
                "USD_SDG",
                "USD_SEK",
                "USD_SGD",
                "USD_SHP",
                "USD_SLL",
                "USD_SOS",
                "USD_SRD",
                "USD_SSP",
                "USD_STD",
                "USD_STN",
                "USD_SVC",
                "USD_SYP",
                "USD_SZL",
                "USD_THB",
                "USD_TJS",
                "USD_TMT",
                "USD_TND",
                "USD_TOP",
                "USD_TRY",
                "USD_TTD",
                "USD_TWD",
                "USD_TZS",
                "USD_UAH",
                "USD_UGX",
                "USD_USD",
                "USD_UYU",
                "USD_UZS",
                "USD_VEF",
                "USD_VES",
                "USD_VND",
                "USD_VUV",
                "USD_WST",
                "USD_XAF",
                "USD_XAG",
                "USD_XAU",
                "USD_XCD",
                "USD_XDR",
                "USD_XOF",
                "USD_XPD",
                "USD_XPF",
                "USD_XPT",
                "USD_YER",
                "USD_ZAR",
                "USD_ZMW",
                "USD_ZWL"
        )
    }
}
