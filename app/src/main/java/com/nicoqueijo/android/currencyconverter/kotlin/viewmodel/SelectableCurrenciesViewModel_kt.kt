package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils

class SelectableCurrenciesViewModel_kt(application: Application) : AndroidViewModel(application) {

    // Candidate for dependency injection
    private val repository = Repository(application)

    private val _allCurrencies = repository.getAllCurrencies()
    val allCurrencies: LiveData<MutableList<Currency>>
        get() = _allCurrencies

    private fun upsertCurrency(currency: Currency) {
        repository.upsertCurrency(currency)
    }

    var adapterSelectableCurrencies: ArrayList<Currency> = ArrayList()
    var adapterFilteredCurrencies: ArrayList<Currency> = ArrayList()

    val searchQuery = MutableLiveData<String>()

    fun handleOnClick(adapterPosition: Int) {
        val selectedCurrency = adapterFilteredCurrencies[adapterPosition]
        val count = adapterSelectableCurrencies.asSequence()
                .filter { it.isSelected }
                .count()
        selectedCurrency.isSelected = true
        selectedCurrency.order = count
        upsertCurrency(selectedCurrency)
    }

    @SuppressLint("DefaultLocale")
    fun filter(constraint: CharSequence?): MutableList<Currency> {
        searchQuery.postValue(constraint.toString())
        val filteredList: MutableList<Currency> = mutableListOf()
        if (constraint == null || constraint.isEmpty()) {
            filteredList.addAll(adapterSelectableCurrencies)
        } else {
            val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }
            adapterSelectableCurrencies.forEach { currency ->
                val currencyCode = currency.trimmedCurrencyCode.toLowerCase()
                val currencyName = Utils.getStringResourceByName(currency.currencyCode, getApplication()).toLowerCase()
                if (currencyCode.contains(filterPattern) || currencyName.contains(filterPattern)) {
                    filteredList.add(currency)
                }
            }
        }
        return filteredList
    }

}