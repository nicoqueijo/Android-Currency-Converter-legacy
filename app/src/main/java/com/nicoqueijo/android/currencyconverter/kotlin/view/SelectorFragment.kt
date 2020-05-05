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
import androidx.transition.ChangeBounds
import com.futuremind.recyclerviewfastscroll.FastScroller
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.adapter.SelectorAdapter
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.SelectorViewModel

class SelectorFragment : Fragment() {

    private lateinit var viewModel: SelectorViewModel

    private lateinit var adapter: SelectorAdapter
    private lateinit var recyclerView: CustomRecyclerView
    private lateinit var noResultsView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_selector, container, false)
        viewModel = ViewModelProvider(this).get(SelectorViewModel::class.java)
        setTransitionDurations()
        setHasOptionsMenu(true)
        initViewsAndAdapter(view)
        observeObservables()
        return view
    }

    private fun setTransitionDurations() {
        sharedElementEnterTransition = ChangeBounds().apply {
            duration = 300
        }
        sharedElementReturnTransition = ChangeBounds().apply {
            duration = 300
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        initMenu(menu, inflater)
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

    private fun initViewsAndAdapter(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view_selector)
        noResultsView = view.findViewById(R.id.no_results)
        recyclerView.showIfEmpty(noResultsView)
        adapter = SelectorAdapter(viewModel)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.setHasFixedSize(true)
        view.findViewById<FastScroller>(R.id.fast_scroller).setRecyclerView(recyclerView)
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
}
