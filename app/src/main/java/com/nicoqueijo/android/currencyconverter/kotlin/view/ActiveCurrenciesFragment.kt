package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.adapter.ActiveCurrenciesAdapter
import com.nicoqueijo.android.currencyconverter.kotlin.util.SwipeAndDragHelper
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.ActiveCurrenciesViewModel


class ActiveCurrenciesFragment : Fragment() {

    private lateinit var viewModel: ActiveCurrenciesViewModel

    private lateinit var recyclerView: CustomRecyclerView
    private lateinit var adapter: ActiveCurrenciesAdapter
    private lateinit var floatingActionButton: FloatingActionButton

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
        recyclerView = view.findViewById(R.id.recycler_view_active_currencies)
        val emptyListView = view.findViewById<View>(R.id.empty_list)
        val keyboard = view.findViewById<DecimalNumberKeyboard>(R.id.keyboard)
        recyclerView.showIfEmpty(emptyListView)
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        initFloatingActionButton(view)
        adapter = ActiveCurrenciesAdapter(viewModel, keyboard)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        val itemTouchHelperCallback = SwipeAndDragHelper(adapter, 0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }

    private fun observeObservables() {
        viewModel.activeCurrencies.observe(viewLifecycleOwner, Observer { currencies ->
            adapter.setCurrencies(currencies)
        })
        viewModel.focusedCurrency.observe(viewLifecycleOwner, Observer { focusedCurrency ->
            recyclerView.smoothScrollToPosition(viewModel.adapterActiveCurrencies.indexOf(focusedCurrency))
        })
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
