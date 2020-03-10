package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.LoadingCurrenciesViewModel_kt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 */
class LoadingCurrenciesFragment_kt : Fragment() {

    private lateinit var viewModel: LoadingCurrenciesViewModel_kt

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loading_currencies_kt, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoadingCurrenciesViewModel_kt::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            // Small delay so the user can actually see the splash screen
            // for a moment as feedback of an attempt to retrieve data.
            delay(250)
            try {
                viewModel.repository.initCurrencies()
                CoroutineScope(Dispatchers.Main).launch {
                    findNavController().navigate(R.id.action_loadingCurrenciesFragment_kt_to_activeCurrenciesFragment_kt)
                }
            } catch (e: IOException) {
                findNavController().navigate(R.id.action_loadingCurrenciesFragment_kt_to_errorFragment_kt)
            }
        }
    }
}
