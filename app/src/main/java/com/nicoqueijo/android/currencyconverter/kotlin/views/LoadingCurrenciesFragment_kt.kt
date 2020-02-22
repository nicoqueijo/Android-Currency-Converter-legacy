package com.nicoqueijo.android.currencyconverter.kotlin.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nicoqueijo.android.currencyconverter.R

/**
 * A simple [Fragment] subclass.
 */
class LoadingCurrenciesFragment_kt : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loading_currencies, container, false)
    }


}
