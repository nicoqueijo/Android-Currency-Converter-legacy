package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.adapter.SelectableCurrenciesAdapter_kt
import com.nicoqueijo.android.currencyconverter.kotlin.util.CustomRecyclerView_kt
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.SelectableCurrenciesViewModel_kt
import com.turingtechnologies.materialscrollbar.AlphabetIndicator
import com.turingtechnologies.materialscrollbar.DragScrollBar

class SelectableCurrenciesFragment_kt : Fragment() {

    private lateinit var viewModel: SelectableCurrenciesViewModel_kt

    private lateinit var adapter: SelectableCurrenciesAdapter_kt
    private lateinit var recyclerView: CustomRecyclerView_kt
    private lateinit var noResultsView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_selectable_currency_kt, container, false)
        viewModel = ViewModelProvider(this).get(SelectableCurrenciesViewModel_kt::class.java)
        initViewsAndAdapter(view)
        observeObservables()
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        initMenu(menu, inflater)
    }

    private fun initViewsAndAdapter(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view_selectable_currencies_kt)
        noResultsView = view.findViewById(R.id.no_results_kt)
        recyclerView.showIfEmpty(noResultsView)
        val dragScrollBar: DragScrollBar = view.findViewById(R.id.drag_scroll_bar_kt)
        dragScrollBar.setIndicator(AlphabetIndicator(context), true)
        adapter = SelectableCurrenciesAdapter_kt(viewModel)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.setHasFixedSize(true)
    }

    private fun observeObservables() {
        viewModel.allCurrencies.observe(viewLifecycleOwner, Observer { currencies ->
            adapter.setCurrencies(currencies)
        })
        viewModel.searchQuery.observe(viewLifecycleOwner, Observer { searchQuery ->
            noResultsView.text = getString(R.string.no_results, searchQuery)
        })
    }

    private fun initMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_kt, menu)
        val searchView = menu.findItem(R.id.search_kt).actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_GO
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }
}
