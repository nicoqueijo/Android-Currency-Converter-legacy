package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.annotation.SuppressLint
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
import com.nicoqueijo.android.currencyconverter.kotlin.adapter.SelectableCurrenciesAdapter
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.SelectableCurrenciesViewModel
import com.turingtechnologies.materialscrollbar.AlphabetIndicator
import com.turingtechnologies.materialscrollbar.DragScrollBar

class SelectableCurrenciesFragment : Fragment() {

    private lateinit var viewModel: SelectableCurrenciesViewModel

    private lateinit var adapter: SelectableCurrenciesAdapter
    private lateinit var recyclerView: CustomRecyclerView
    private lateinit var noResultsView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_selectable_currency, container, false)
        viewModel = ViewModelProvider(this).get(SelectableCurrenciesViewModel::class.java)
        initViewsAndAdapter(view)
        observeObservables()
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        initMenu(menu, inflater)
    }

    private fun initViewsAndAdapter(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view_selectable_currencies)
        noResultsView = view.findViewById(R.id.no_results)
        recyclerView.showIfEmpty(noResultsView)
        val dragScrollBar = view.findViewById<DragScrollBar>(R.id.drag_scroll_bar)
        dragScrollBar.setIndicator(AlphabetIndicator(context), true)
        adapter = SelectableCurrenciesAdapter(viewModel)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.setHasFixedSize(true)
    }

    @SuppressLint("StringFormatInvalid")
    private fun observeObservables() {
        viewModel.allCurrencies.observe(viewLifecycleOwner, Observer { currencies ->
            adapter.setCurrencies(currencies)
        })
        viewModel.searchQuery.observe(viewLifecycleOwner, Observer { searchQuery ->
            noResultsView.text = getString(R.string.no_results, searchQuery)
        })
    }

    private fun initMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        val searchView = menu.findItem(R.id.search).actionView as SearchView
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
