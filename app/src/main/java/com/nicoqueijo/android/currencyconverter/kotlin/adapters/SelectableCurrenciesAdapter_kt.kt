package com.nicoqueijo.android.currencyconverter.kotlin.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.java.helpers.Utility
import com.nicoqueijo.android.currencyconverter.kotlin.models.Currency


// Also implement Filterable and INameableAdapter
class SelectableCurrenciesAdapter_kt(val context: Context?,
                                     val currencies: List<Currency>) :
        RecyclerView.Adapter<SelectableCurrenciesAdapter_kt.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        var flag: ImageView = itemView.findViewById(R.id.flag_kt)
        var currencyCode: TextView = itemView.findViewById(R.id.currency_code_kt)
        var currencyName: TextView = itemView.findViewById(R.id.currency_name_kt)
        var checkMark: ImageView = itemView.findViewById(R.id.check_mark_kt)

        override fun onClick(v: View?) {
            v?.findNavController()?.popBackStack()
//            v?.findNavController()?.navigate(R.id.action_selectableCurrenciesFragment_kt_to_activeCurrenciesFragment_kt)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_selectable_currency_kt, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return currencies.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.flag.setImageResource(Utility.getDrawableResourceByName(currencies[position]
                .currencyCode.toLowerCase(), context))
        holder.currencyCode.text = currencies[position].trimmedCurrencyCode
        holder.currencyName.text = Utility.getStringResourceByName(currencies[position]
                .currencyCode, context)
        val currencyIsSelected: Boolean = currencies[position].isSelected
        holder.itemView.isClickable = !currencyIsSelected
        holder.checkMark.visibility = if (currencyIsSelected) View.VISIBLE else View.INVISIBLE
    }

}