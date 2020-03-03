package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.java.models.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    val repository: Repository = Repository(application)

    lateinit var currencies: MutableLiveData<List<Currency>>

     var _activeFragment: MutableLiveData<Int> = MutableLiveData(R.id.loadingCurrenciesFragment_kt)

    val activeFragment: LiveData<Int>
        get() = _activeFragment

}