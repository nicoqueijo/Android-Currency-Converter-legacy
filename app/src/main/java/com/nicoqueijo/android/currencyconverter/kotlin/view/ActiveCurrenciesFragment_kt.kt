package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.adapter.ActiveCurrenciesAdapter_kt
import com.nicoqueijo.android.currencyconverter.kotlin.util.CustomRecyclerView_kt
import com.nicoqueijo.android.currencyconverter.kotlin.util.SwipeAndDragHelper_kt
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.ActiveCurrenciesViewModel_kt
import java.text.DecimalFormatSymbols

class ActiveCurrenciesFragment_kt : Fragment() {

    private lateinit var viewModel: ActiveCurrenciesViewModel_kt

    private lateinit var adapter: ActiveCurrenciesAdapter_kt
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var buttonOne: Button
    private lateinit var buttonTwo: Button
    private lateinit var buttonThree: Button
    private lateinit var buttonFour: Button
    private lateinit var buttonFive: Button
    private lateinit var buttonSix: Button
    private lateinit var buttonSeven: Button
    private lateinit var buttonEight: Button
    private lateinit var buttonNine: Button
    private lateinit var buttonDecimalSeparator: Button
    private lateinit var buttonZero: Button
    private lateinit var buttonBackspace: ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_active_currencies_kt, container, false)
        viewModel = ViewModelProvider(this).get(ActiveCurrenciesViewModel_kt::class.java)
        initViewsAndAdapter(view)
        observeObservables()
        populateDefaultCurrencies()
        setUpFabOnClickListener()
        return view
    }

    private fun initViewsAndAdapter(view: View) {
        val recyclerView: CustomRecyclerView_kt = view.findViewById(R.id.recycler_view_active_currencies_kt)
        val emptyListView = view.findViewById<View>(R.id.empty_list_kt)
        recyclerView.showIfEmpty(emptyListView)
        initButtons(view)
        adapter = ActiveCurrenciesAdapter_kt(viewModel)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback = SwipeAndDragHelper_kt(adapter,
                0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }

    private fun initButtons(view: View) {
        floatingActionButton = view.findViewById(R.id.floating_action_button_kt)
        buttonOne = view.findViewById(R.id.button_one)
        buttonTwo = view.findViewById(R.id.button_two)
        buttonThree = view.findViewById(R.id.button_three)
        buttonFour = view.findViewById(R.id.button_four)
        buttonFive = view.findViewById(R.id.button_five)
        buttonSix = view.findViewById(R.id.button_six)
        buttonSeven = view.findViewById(R.id.button_seven)
        buttonEight = view.findViewById(R.id.button_eight)
        buttonNine = view.findViewById(R.id.button_nine)
        buttonDecimalSeparator = view.findViewById(R.id.button_decimal_separator)
        buttonZero = view.findViewById(R.id.button_zero)
        buttonBackspace = view.findViewById(R.id.button_backspace)
        buttonDecimalSeparator.text = DecimalFormatSymbols.getInstance().decimalSeparator.toString()
        styleButtons(buttonOne, buttonTwo, buttonThree, buttonFour, buttonFive,
                buttonSix, buttonSeven, buttonEight, buttonNine, buttonDecimalSeparator,
                buttonZero, buttonBackspace)
    }

    private fun styleButtons(vararg buttons: View) {
        buttons.forEach { it.setBackgroundResource(R.drawable.background_button) }
    }

    private fun observeObservables() {
        viewModel.activeCurrencies.observe(viewLifecycleOwner, Observer { currencies ->
            adapter.setCurrencies(currencies)
        })
        /*viewModel.focusedCurrency.observe(viewLifecycleOwner, Observer {
            adapter.updateHints()
        })*/
    }

    private fun setUpFabOnClickListener() {
        floatingActionButton.setOnClickListener {
            Utils.hideKeyboard(activity)
            findNavController().navigate(R.id.action_activeCurrenciesFragment_kt_to_selectableCurrenciesFragment_kt)
        }
    }

    private fun populateDefaultCurrencies() {
        viewModel.populateDefaultCurrencies()
    }
}
