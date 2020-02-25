package com.nicoqueijo.android.currencyconverter.kotlin.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nicoqueijo.android.currencyconverter.R
import com.turingtechnologies.materialscrollbar.AlphabetIndicator
import com.turingtechnologies.materialscrollbar.DragScrollBar


// Highlight 'Converter' in menu when we are in this Fragment
class SelectableCurrenciesFragment_kt : Fragment() {

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        toolbar = activity!!.findViewById(R.id.toolbar_kt)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_selectable_currency_kt, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_selectable_currencies_kt)
//        val dragScrollBar = view.findViewById<DragScrollBar>(R.id.drag_scroll_bar_kt)
//        dragScrollBar.setIndicator(AlphabetIndicator(context), true)
        // init adapter
        val layoutManager = LinearLayoutManager(context)
        return view
    }

}
