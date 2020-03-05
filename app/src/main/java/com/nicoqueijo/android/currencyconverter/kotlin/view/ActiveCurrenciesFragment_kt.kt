package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.adapter.ActiveCurrenciesAdapter_kt
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.CustomRecyclerView_kt

class ActiveCurrenciesFragment_kt : Fragment() {

    private lateinit var adapter: ActiveCurrenciesAdapter_kt
    private lateinit var interstitialAd: InterstitialAd
    private lateinit var floatingActionButton: FloatingActionButton
    private var fabClicks = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_active_currencies_kt, container, false)
        initInterstitialAd()
        initViewsAndAdapters(view)
        setUpFabOnClickListener()
        return view
    }

    private fun initViewsAndAdapters(view: View) {
        val recyclerView: CustomRecyclerView_kt = view.findViewById(R.id.recycler_view_active_currencies_kt)
        val emptyListView = view.findViewById<View>(R.id.container_empty_list_kt)
        recyclerView.showIfEmpty(emptyListView)
        floatingActionButton = view.findViewById(R.id.floating_action_button_kt)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        adapter = ActiveCurrenciesAdapter_kt(context, MainActivity_kt.activityViewModel.currencies, floatingActionButton)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
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
            hideKeyboard()
            findNavController().navigate(R.id.action_activeCurrenciesFragment_kt_to_selectableCurrenciesFragment_kt)
        }
    }

    private fun processInterstitialAd() {
        if (fabClicks == 1) {
            showInterstitialAd()
        } else if (fabClicks % 5 == 0 && fabClicks != 0) {
            showInterstitialAd()
        }
        fabClicks++
    }

    private fun showInterstitialAd() {
        if (interstitialAd.isLoaded) {
            interstitialAd.show()
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
    }
}
