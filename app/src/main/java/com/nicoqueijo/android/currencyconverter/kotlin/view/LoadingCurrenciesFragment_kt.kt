package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nicoqueijo.android.currencyconverter.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class LoadingCurrenciesFragment_kt : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loading_currencies_kt, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        CoroutineScope(Dispatchers.Main).launch {
//            MainActivity_kt.mockExpensiveCall()
//            val random = java.util.Random().nextBoolean()
//            if (random) {
//                findNavController().navigate(R.id.action_loadingCurrenciesFragment_kt_to_errorFragment_kt)
//            } else {
//                findNavController().navigate(R.id.action_loadingCurrenciesFragment_kt_to_activeCurrenciesFragment_kt)
//            }
//        }

    }


}
