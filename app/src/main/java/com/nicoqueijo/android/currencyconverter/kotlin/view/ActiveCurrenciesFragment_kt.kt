package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.adapter.ActiveCurrenciesAdapter_kt
import com.nicoqueijo.android.currencyconverter.kotlin.util.CustomRecyclerView_kt
import com.nicoqueijo.android.currencyconverter.kotlin.util.SwipeAndDragHelper_kt
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.ActiveCurrenciesViewModel_kt

class ActiveCurrenciesFragment_kt : Fragment() {

    private lateinit var viewModel: ActiveCurrenciesViewModel_kt

    private lateinit var adapter: ActiveCurrenciesAdapter_kt
    private lateinit var interstitialAd: InterstitialAd
    private lateinit var floatingActionButton: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_active_currencies_kt, container, false)
        viewModel = ViewModelProvider(this).get(ActiveCurrenciesViewModel_kt::class.java)
        initInterstitialAd()
        initViewsAndAdapter(view)
        setUpFabOnClickListener()
        return view
    }

    private fun initViewsAndAdapter(view: View) {
        val recyclerView: CustomRecyclerView_kt = view.findViewById(R.id.recycler_view_active_currencies_kt)
        val emptyListView = view.findViewById<View>(R.id.container_empty_list_kt)
        recyclerView.showIfEmpty(emptyListView)
        floatingActionButton = view.findViewById(R.id.floating_action_button_kt)
        adapter = ActiveCurrenciesAdapter_kt(viewModel, floatingActionButton)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback = SwipeAndDragHelper_kt(adapter,
                0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
        viewModel.activeCurrencies.observe(viewLifecycleOwner, Observer { currencies ->
            adapter.setCurrencies(currencies)
        })
    }

    private fun initInterstitialAd() {
        interstitialAd = InterstitialAd(activity)
        interstitialAd.adUnitId = resources.getString(R.string.ad_unit_id_interstitial_test)
        interstitialAd.loadAd(AdRequest.Builder().build())
        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                interstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
    }

    private fun setUpFabOnClickListener() {
        floatingActionButton.setOnClickListener {
            processInterstitialAd()
            Utils.hideKeyboard(activity)
            findNavController().navigate(R.id.action_activeCurrenciesFragment_kt_to_selectableCurrenciesFragment_kt)
        }
    }

    private fun processInterstitialAd() {
        if (viewModel.fabClicks == 1) {
            showInterstitialAd()
        } else if (viewModel.fabClicks % 5 == 0 && viewModel.fabClicks != 0) {
            showInterstitialAd()
        }
        viewModel.fabClicks++
    }

    private fun showInterstitialAd() {
        if (interstitialAd.isLoaded) {
            interstitialAd.show()
        }
    }

    companion object {
        const val TAG = "ActiveCurrencies"
    }
}
