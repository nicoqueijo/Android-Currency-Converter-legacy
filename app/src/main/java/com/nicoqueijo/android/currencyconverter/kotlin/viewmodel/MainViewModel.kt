package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nicoqueijo.android.currencyconverter.BuildConfig.BUILD_TYPE
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.data.DefaultRepository
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import java.text.SimpleDateFormat
import java.util.*

@ActivityRetainedScoped
class MainViewModel @ViewModelInject constructor(
        private val repository: Repository,
        application: Application) : AndroidViewModel(application) {

    val activeFragment = MutableLiveData(R.id.splashFragment)

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

    companion object {
        const val REMOVE_ADS_PRODUCT_ID = "remove_ads"
        var adsEnabled = true
    }
}