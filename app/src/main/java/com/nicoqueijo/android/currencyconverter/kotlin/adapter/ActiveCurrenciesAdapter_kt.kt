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
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.CustomEditText_kt
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils


class ActiveCurrenciesAdapter_kt(val context: Context?,
                                 val activeCurrencies: List<Currency>,
                                 val floatingActionButton: FloatingActionButton) :
        RecyclerView.Adapter<ActiveCurrenciesAdapter_kt.ViewHolder>() {

//    var onBind: Boolean = false

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rowBackground: ConstraintLayout = itemView.findViewById(R.id.row_background_kt)
        val rowForeground: ConstraintLayout = itemView.findViewById(R.id.row_foreground_kt)
        val deleteIconStart: ImageView = itemView.findViewById(R.id.delete_icon_start_kt)
        val deleteIconEnd: ImageView = itemView.findViewById(R.id.delete_icon_end_kt)
        val flag: ImageView = itemView.findViewById(R.id.flag_kt)
        val currencyCode: TextView = itemView.findViewById(R.id.currency_code_kt)
        val conversionValue: CustomEditText_kt = itemView.findViewById(R.id.conversion_value_kt)

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
        val currency = activeCurrencies[position]
        with(holder) {
            currencyCode.text = currency.trimmedCurrencyCode
            flag.setImageResource(Utils.getDrawableResourceByName(currency.currencyCode.toLowerCase(), context))
        }
//        mOnBind = false
    }
}