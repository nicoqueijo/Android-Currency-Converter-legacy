package com.nicoqueijo.android.currencyconverter.kotlin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.databinding.RowActiveCurrencyKtBinding
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.SwipeAndDragHelper_kt


class ActiveCurrenciesAdapter_kt(val context: Context?) :
        RecyclerView.Adapter<ActiveCurrenciesAdapter_kt.ViewHolder>(),
        SwipeAndDragHelper_kt.ActionCompletionContract {

    private var activeCurrencies = mutableListOf<Currency>()

//    var onBind: Boolean = false

    inner class ViewHolder(val binding: RowActiveCurrencyKtBinding) : RecyclerView.ViewHolder(binding.root) {

        val rowBackground: ConstraintLayout = itemView.findViewById(R.id.row_background_kt)
        val rowForeground: ConstraintLayout = itemView.findViewById(R.id.row_foreground_kt)
        val deleteIconStart: ImageView = itemView.findViewById(R.id.delete_icon_start_kt)
        val deleteIconEnd: ImageView = itemView.findViewById(R.id.delete_icon_end_kt)
        val flag: ImageView = itemView.findViewById(R.id.flag_kt)
        val currencyCode: TextView = itemView.findViewById(R.id.currency_code_kt)
//        val conversionValue: CustomEditText_kt = itemView.findViewById(R.id.conversion_value_kt)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowActiveCurrencyKtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return activeCurrencies.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.currency = activeCurrencies[position]

//        mOnBind = true
//        val currency = currencies[position]
//        with(holder) {
//            currencyCode.text = currency.trimmedCurrencyCode
//            flag.setImageResource(Utils.getDrawableResourceByName(currency.currencyCode.toLowerCase(), context))
//        }
//        mOnBind = false
    }

    fun setCurrencies(currencies: MutableList<Currency>) {
        this.activeCurrencies = currencies
        notifyDataSetChanged()
    }

    override fun onViewMoved(oldPosition: Int, newPosition: Int) {
        val movingCurrency: Currency = activeCurrencies[oldPosition]
        activeCurrencies.removeAt(oldPosition)
        activeCurrencies.add(newPosition, movingCurrency)
        notifyItemMoved(oldPosition, newPosition)
    }

    override fun onViewSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {

    }
}