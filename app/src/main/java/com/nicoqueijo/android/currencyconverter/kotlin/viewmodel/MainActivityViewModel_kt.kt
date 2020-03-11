package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nicoqueijo.android.currencyconverter.R

class MainActivityViewModel_kt(application: Application) : AndroidViewModel(application) {

    val _activeFragment = MutableLiveData(R.id.loadingCurrenciesFragment_kt)
    val activeFragment: LiveData<Int>
        get() = _activeFragment

    val fragmentBackstackEntries: MutableSet<Int> = mutableSetOf()

}