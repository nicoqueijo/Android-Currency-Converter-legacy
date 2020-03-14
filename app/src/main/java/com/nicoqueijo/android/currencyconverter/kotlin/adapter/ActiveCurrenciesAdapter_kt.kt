package com.nicoqueijo.android.currencyconverter.kotlin.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.databinding.RowActiveCurrencyKtBinding
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.CurrencyDiffUtilCallback
import com.nicoqueijo.android.currencyconverter.kotlin.util.SwipeAndDragHelper_kt
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.ActiveCurrenciesViewModel_kt


class ActiveCurrenciesAdapter_kt(private val viewModel: ActiveCurrenciesViewModel_kt,
                                 private val floatingActionButton: FloatingActionButton) :
        ListAdapter<Currency, ActiveCurrenciesAdapter_kt.ViewHolder>(CurrencyDiffUtilCallback()),
        SwipeAndDragHelper_kt.ActionCompletionContract {

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

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.currency = viewModel.adapterActiveCurrencies[position]
    }

    fun setCurrencies(currencies: MutableList<Currency>) {
        viewModel.adapterActiveCurrencies = currencies
        submitList(viewModel.adapterActiveCurrencies)
    }

    override fun onViewMoved(oldPosition: Int, newPosition: Int) {
        viewModel.handleMove(oldPosition, newPosition)
        setCurrencies(viewModel.adapterActiveCurrencies)
    }

    override fun onViewSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
//        val conversionValue = swipedCurrency.conversionValue
        viewModel.handleSwipe(position)
        Snackbar.make(floatingActionButton, R.string.item_removed, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) {
                    viewModel.handleSwipeUndo()
                }.show()
    }
}