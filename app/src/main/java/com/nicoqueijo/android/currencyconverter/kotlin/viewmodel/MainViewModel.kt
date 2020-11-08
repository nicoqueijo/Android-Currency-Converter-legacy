package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nicoqueijo.android.currencyconverter.BuildConfig.BUILD_TYPE
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository.Companion.DEBUG
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository.Companion.RELEASE
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.toMillis
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
        val timestampInMillis = repository.timestampInSeconds.toMillis()
        val date = Date(timestampInMillis)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        simpleDateFormat.timeZone = TimeZone.getDefault()
        return simpleDateFormat.format(date)
    }

    fun getBannerAdId(context: Context): String {
        with(context.resources) {
            return when (BUILD_TYPE) {
                RELEASE -> getString(R.string.ad_unit_id_banner)
                DEBUG -> getString(R.string.ad_unit_id_banner_test)
                else -> getString(R.string.ad_unit_id_banner)
            }
        }
    }

    companion object {
        const val REMOVE_ADS_PRODUCT_ID = "remove_ads"
        var adsEnabled = true
    }
}