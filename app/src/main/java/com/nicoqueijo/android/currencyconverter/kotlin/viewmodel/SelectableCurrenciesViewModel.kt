package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nicoqueijo.android.currencyconverter.BuildConfig
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils
import java.util.*
import kotlin.collections.ArrayList

class SelectableCurrenciesViewModel(application: Application) : AndroidViewModel(application) {

    // Candidate for dependency injection
    private val repository = Repository(application)
    val allCurrencies = repository.getAllCurrencies()
    private fun upsertCurrency(currency: Currency) {
        repository.upsertCurrency(currency)
    }

    var adapterSelectableCurrencies: ArrayList<Currency> = ArrayList()
    var adapterFilteredCurrencies: ArrayList<Currency> = ArrayList()
    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String>
        get() = _searchQuery

    // 1 in 12 chance the user will be shown an interstitial ad when they select a currency.
    val willShowAd = (1..12).random() == 1
    fun getInterstitialAdId(context: Context): String {
        with(context.resources) {
            return when (BuildConfig.BUILD_TYPE) {
                "release" -> getString(R.string.ad_unit_id_interstitial)
                "debug" -> getString(R.string.ad_unit_id_interstitial_test)
                else -> getString(R.string.ad_unit_id_interstitial)
            }
        }
    }

    /**
     * Takes the clicked currency, updates its selected value to true and its order value as the
     * last value of the already-selected currencies. Upserts that to the database so its observers
     * can be notified.
     */
    fun handleOnClick(adapterPosition: Int) {
        val selectedCurrency = adapterFilteredCurrencies[adapterPosition]
        val countOfSelectedCurrencies = adapterSelectableCurrencies.asSequence()
                .filter { it.isSelected }
                .count()
        selectedCurrency.isSelected = true
        selectedCurrency.order = countOfSelectedCurrencies
        upsertCurrency(selectedCurrency)
    }

    fun filter(constraint: CharSequence?): MutableList<Currency> {
        _searchQuery.postValue(constraint.toString())
        val filteredList: MutableList<Currency> = mutableListOf()
        if (constraint == null || constraint.isEmpty()) {
            filteredList.addAll(adapterSelectableCurrencies)
        } else {
            val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim { it <= ' ' }
            adapterSelectableCurrencies.forEach { currency ->
                val currencyCode = currency.trimmedCurrencyCode.toLowerCase(Locale.ROOT)
                val currencyName = Utils.getStringResourceByName(currency.currencyCode, getApplication())
                        .toLowerCase(Locale.ROOT)
                if (currencyCode.contains(filterPattern) || currencyName.contains(filterPattern)) {
                    filteredList.add(currency)
                }
            }
        }
        return filteredList
    }
}