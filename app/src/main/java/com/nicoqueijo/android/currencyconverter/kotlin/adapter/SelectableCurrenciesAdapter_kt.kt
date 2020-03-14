package com.nicoqueijo.android.currencyconverter.kotlin.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nicoqueijo.android.currencyconverter.databinding.RowSelectableCurrencyKtBinding
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.CurrencyDiffUtilCallback
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.SelectableCurrenciesViewModel_kt
import com.turingtechnologies.materialscrollbar.INameableAdapter

class SelectableCurrenciesAdapter_kt(private val viewModel: SelectableCurrenciesViewModel_kt) :
        ListAdapter<Currency, SelectableCurrenciesAdapter_kt.ViewHolder>(CurrencyDiffUtilCallback()),
        INameableAdapter,
        Filterable {

    inner class ViewHolder(val binding: RowSelectableCurrencyKtBinding) :
            RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            viewModel.handleOnClick(adapterPosition)
            v?.findNavController()?.popBackStack()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowSelectableCurrencyKtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.currency = viewModel.adapterFilteredCurrencies[position]
    }

    fun setCurrencies(currencies: List<Currency>) {
        viewModel.adapterFilteredCurrencies = ArrayList(currencies)
        viewModel.adapterSelectableCurrencies = ArrayList(currencies)
        submitList(viewModel.adapterFilteredCurrencies)
    }

    override fun getCharacterForElement(position: Int): Char {
        return try {
            viewModel.adapterFilteredCurrencies[position].currencyCode[Currency.CURRENCY_CODE_STARTING_INDEX]
        } catch (exception: IndexOutOfBoundsException) {
            ' '
        }
    }

    override fun getFilter() = currenciesFilter

    private val currenciesFilter: Filter = object : Filter() {
        @SuppressLint("DefaultLocale")
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            filterResults.values = viewModel.filter(constraint)
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            with(viewModel.adapterFilteredCurrencies) {
                clear()
                addAll(results.values as List<Currency>)
            }
            notifyDataSetChanged()
        }
    }
}