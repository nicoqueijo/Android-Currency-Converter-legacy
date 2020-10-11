package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nicoqueijo.android.currencyconverter.R

class ErrorFragment : Fragment(R.layout.fragment_error) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_refresh, menu)
        initMenuItem(menu)
    }

    private fun initMenuItem(menu: Menu) {
        (menu.findItem(R.id.refresh).actionView as ImageView).apply {
            setImageResource(R.drawable.ic_refresh)
            setPadding(24, 24, 24, 24)
            setOnClickListener {
                findNavController().navigate(R.id.action_errorFragment_to_splashFragment)
            }
        }
    }
}
