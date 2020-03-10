package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nicoqueijo.android.currencyconverter.R

class ErrorFragment_kt : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_error_kt, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_refresh_kt, menu)
        initMenuItem(menu)
    }

    private fun initMenuItem(menu: Menu) {
        val refreshMenuItem = menu.findItem(R.id.refresh_kt).actionView as ImageView
        refreshMenuItem.setImageResource(R.drawable.ic_refresh)
        refreshMenuItem.setPadding(24, 24, 24, 24)
        refreshMenuItem.setOnClickListener {
            findNavController().navigate(R.id.action_errorFragment_kt_to_loadingCurrenciesFragment_kt)
        }
    }
}
