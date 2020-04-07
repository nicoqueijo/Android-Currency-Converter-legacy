package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository
import java.text.SimpleDateFormat
import java.util.*

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    // Candidate for dependency injection
    private val repository = Repository(application)

    val activeFragment = MutableLiveData(R.id.loadingCurrenciesFragment)

    val fragmentBackstackEntries: MutableSet<Int> = mutableSetOf()

    @SuppressLint("SimpleDateFormat")
    fun getFormattedLastUpdate(): String {
        val timestamp = repository.timestamp
        val date = Date(timestamp)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        simpleDateFormat.timeZone = TimeZone.getDefault()
        return simpleDateFormat.format(date)
    }
}