package com.nicoqueijo.android.currencyconverter.kotlin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.databinding.RowActiveCurrencyKtBinding
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.SwipeAndDragHelper_kt
import com.nicoqueijo.android.currencyconverter.kotlin.view.MainActivity_kt
import java.math.BigDecimal


class ActiveCurrenciesAdapter_kt(val context: Context?, private val floatingActionButton: FloatingActionButton) :
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
        val swipedCurrency = activeCurrencies[position]
        val swipedCurrencyOrder = swipedCurrency.order
//        val conversionValue = swipedCurrency.conversionValue

        activeCurrencies.reversed()
                .forEach { currency ->
                    if (currency.order == swipedCurrencyOrder) {
                        return@forEach
                    }
                    currency.order--
                    Log.d("Nico", "Shifting (swipe): $currency")
                    MainActivity_kt.activityViewModel.upsertCurrency(currency)
                }

        swipedCurrency.isSelected = false
        swipedCurrency.order = -1
        swipedCurrency.conversionValue = BigDecimal(0.0)
        Log.d("Nico", "Swiped: $swipedCurrency")
        MainActivity_kt.activityViewModel.upsertCurrency(swipedCurrency)
        Log.d("Nico", "activeCurrencies after Swipe: $activeCurrencies")

//        activeCurrencies.removeAt(position)
//        notifyItemRemoved(position)

        val snackbar = Snackbar.make(floatingActionButton, R.string.item_removed, Snackbar.LENGTH_LONG)
        snackbar.setAction(R.string.undo) {
//            swipedCurrency.conversionValue = conversionValue
            swipedCurrency.isSelected = true
            swipedCurrency.order = swipedCurrencyOrder
            Log.d("Nico", "Recovered: $swipedCurrency")
            MainActivity_kt.activityViewModel.upsertCurrency(swipedCurrency)

            for (i in swipedCurrencyOrder until activeCurrencies.size) {
                val currency = activeCurrencies[i]
                currency.order++
                Log.d("Nico", "Shifting (undo): $currency")
                MainActivity_kt.activityViewModel.upsertCurrency(currency)
            }
            Log.d("Nico", "activeCurrencies after Undo: $activeCurrencies")

//            notifyItemInserted(position)
        }




        snackbar.show()
    }
}