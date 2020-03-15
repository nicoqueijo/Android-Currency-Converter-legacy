package com.nicoqueijo.android.currencyconverter.kotlin.util

import androidx.recyclerview.widget.DiffUtil
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency

class CurrencyDiffUtilCallback : DiffUtil.ItemCallback<Currency>() {
    override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean {
        return oldItem.currencyCode == newItem.currencyCode
    }

    override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean {
        return oldItem.currencyCode == newItem.currencyCode &&
                oldItem.exchangeRate == newItem.exchangeRate
    }
}