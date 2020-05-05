package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nicoqueijo.android.currencyconverter.BuildConfig.BUILD_TYPE
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository
import java.text.SimpleDateFormat
import java.util.*

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(application)

    val activeFragment = MutableLiveData(R.id.splashFragment)
    val fragmentBackstackEntries: MutableSet<Int> = mutableSetOf()

    @SuppressLint("SimpleDateFormat")
    fun getFormattedLastUpdate(): String {
        val timestamp = repository.timestamp
        val date = Date(timestamp)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        simpleDateFormat.timeZone = TimeZone.getDefault()
        return simpleDateFormat.format(date)
    }

    fun getBannerAdId(context: Context): String {
        with(context.resources) {
            return when (BUILD_TYPE) {
                "release" -> getString(R.string.ad_unit_id_banner)
                "debug" -> getString(R.string.ad_unit_id_banner_test)
                else -> getString(R.string.ad_unit_id_banner)
            }
        }
    }
}