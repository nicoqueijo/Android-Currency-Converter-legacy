package com.nicoqueijo.android.currencyconverter.kotlin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.common.collect.Lists
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils
import com.turingtechnologies.materialscrollbar.INameableAdapter

class SelectableCurrenciesAdapter_kt(val context: Context?) :
        RecyclerView.Adapter<SelectableCurrenciesAdapter_kt.ViewHolder>(),
        INameableAdapter,
        Filterable {

    private lateinit var currenciesFull: ArrayList<Currency>
    private var currencies = ArrayList(currenciesFull)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        val flag: ImageView = itemView.findViewById(R.id.flag_kt)
        val currencyCode: TextView = itemView.findViewById(R.id.currency_code_kt)
        val currencyName: TextView = itemView.findViewById(R.id.currency_name_kt)
        val checkMark: ImageView = itemView.findViewById(R.id.check_mark_kt)

        override fun onClick(v: View?) {
            v?.findNavController()?.popBackStack()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_selectable_currency_kt, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return currencies.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currency = currencies[position]
        val isSelected = currency.isSelected
        val code = currency.currencyCode
        with(holder) {
            itemView.isClickable = !isSelected
            checkMark.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE
            currencyCode.text = currency.trimmedCurrencyCode
            currencyName.text = Utils.getStringResourceByName(code, context)
            flag.setImageResource(Utils.getDrawableResourceByName(code.toLowerCase(), context))
        }
    }

    fun setCurrencies(currencies: List<Currency>) {
        this.currenciesFull = ArrayList(currencies)
    }

    override fun getCharacterForElement(position: Int): Char {
        return try {
            currencies[position].currencyCode[Currency.CURRENCY_CODE_STARTING_INDEX]
        } catch (exception: IndexOutOfBoundsException) {
            ' '
        }
    }

    override fun getFilter() = currenciesFilter

    private val currenciesFilter: Filter = object : Filter() {
        @SuppressLint("DefaultLocale")
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: MutableList<Currency> = Lists.newArrayList()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(currenciesFull)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }
                currenciesFull.forEach { currency ->
                    val currencyCode = currency.trimmedCurrencyCode.toLowerCase()
                    val currencyName = Utils.getStringResourceByName(currency.currencyCode, context).toLowerCase()
                    if (currencyCode.contains(filterPattern) || currencyName.contains(filterPattern)) {
                        filteredList.add(currency)
                    }
                }
            }
            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            with(currencies) {
                clear()
                addAll(results.values as List<Currency>)
            }
            notifyDataSetChanged()
        }
    }

}