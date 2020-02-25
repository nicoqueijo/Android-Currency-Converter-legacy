package com.nicoqueijo.android.currencyconverter.kotlin.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nicoqueijo.android.currencyconverter.R

class ActiveCurrenciesFragment_kt : Fragment() {

    lateinit var floatingActionButton: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_active_currencies_kt, container, false)
        floatingActionButton = view.findViewById(R.id.floating_action_button_kt)
        floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_activeCurrenciesFragment_kt_to_selectableCurrenciesFragment_kt)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
