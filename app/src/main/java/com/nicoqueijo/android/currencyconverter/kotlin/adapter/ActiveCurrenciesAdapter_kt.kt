package com.nicoqueijo.android.currencyconverter.kotlin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.java.helpers.Utility
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.CustomEditText_kt


class ActiveCurrenciesAdapter_kt(val context: Context?,
                                 val activeCurrencies: List<Currency>,
                                 val floatingActionButton: FloatingActionButton) :
        RecyclerView.Adapter<ActiveCurrenciesAdapter_kt.ViewHolder>() {

//    var onBind: Boolean = false

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rowBackground: ConstraintLayout = itemView.findViewById(R.id.row_background_kt)
        var rowForeground: ConstraintLayout = itemView.findViewById(R.id.row_foreground_kt)
        var deleteIconStart: ImageView = itemView.findViewById(R.id.delete_icon_start_kt)
        var deleteIconEnd: ImageView = itemView.findViewById(R.id.delete_icon_end_kt)
        var flag: ImageView = itemView.findViewById(R.id.flag_kt)
        var currencyCode: TextView = itemView.findViewById(R.id.currency_code_kt)
        var conversionValue: CustomEditText_kt = itemView.findViewById(R.id.conversion_value_kt)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_active_currency_kt, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return activeCurrencies.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        mOnBind = true
        val currentCurrency: Currency = activeCurrencies[position]
        holder.currencyCode.text = currentCurrency.trimmedCurrencyCode
        holder.flag.setImageResource(Utility.getDrawableResourceByName(currentCurrency
                .currencyCode.toLowerCase(), context))
//        mOnBind = false
    }
}