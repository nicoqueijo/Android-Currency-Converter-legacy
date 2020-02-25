package com.nicoqueijo.android.currencyconverter.kotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

// Also implement Filterable and INameableAdapter
class SelectableCurrenciesAdapter_kt : RecyclerView.Adapter<SelectableCurrenciesAdapter_kt.ViewHolder>() {

    val list = listOf("One", "Two", "Three")

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }


}