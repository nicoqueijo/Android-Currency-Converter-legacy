package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nicoqueijo.android.currencyconverter.R

class ErrorFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_error, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_refresh, menu)
        initMenuItem(menu)
    }

    private fun initMenuItem(menu: Menu) {
        val refreshMenuItem = menu.findItem(R.id.refresh).actionView as ImageView
        refreshMenuItem.setImageResource(R.drawable.ic_refresh)
        refreshMenuItem.setPadding(24, 24, 24, 24)
        refreshMenuItem.setOnClickListener {
            findNavController().navigate(R.id.action_errorFragment_to_splashFragment)
        }
    }
}
