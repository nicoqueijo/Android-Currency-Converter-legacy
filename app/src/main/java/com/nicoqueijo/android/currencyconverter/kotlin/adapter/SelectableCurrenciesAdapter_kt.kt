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
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.common.collect.Lists
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.java.helpers.Utility
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.turingtechnologies.materialscrollbar.INameableAdapter

class SelectableCurrenciesAdapter_kt(val context: Context?,
                                     currenciesArg: MutableLiveData<List<Currency>>) :
        RecyclerView.Adapter<SelectableCurrenciesAdapter_kt.ViewHolder>(),
        INameableAdapter,
        Filterable {

    private var currencies: ArrayList<Currency> = ArrayList(currenciesArg)
    private val currenciesFull: ArrayList<Currency> = ArrayList(currenciesArg)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        var flag: ImageView = itemView.findViewById(R.id.flag_kt)
        var currencyCode: TextView = itemView.findViewById(R.id.currency_code_kt)
        var currencyName: TextView = itemView.findViewById(R.id.currency_name_kt)
        var checkMark: ImageView = itemView.findViewById(R.id.check_mark_kt)

        override fun onClick(v: View?) {
            v?.findNavController()?.popBackStack()
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
                    val currencyName = Utility.getStringResourceByName(currency.currencyCode, context).toLowerCase()
                    if (currencyCode.contains(filterPattern) ||currencyName.contains(filterPattern)) {
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