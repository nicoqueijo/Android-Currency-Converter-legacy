package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository

class MainActivityViewModel_kt(application: Application) : AndroidViewModel(application) {

    // Candidate for dependency injection
    private val repository = Repository(application)

    val _activeFragment = MutableLiveData(R.id.loadingCurrenciesFragment_kt)
    val activeFragment: LiveData<Int>
        get() = _activeFragment

    val fragmentBackstackEntries: MutableSet<Int> = mutableSetOf()

    fun getLastUpdate() = repository.lastUpdate
}