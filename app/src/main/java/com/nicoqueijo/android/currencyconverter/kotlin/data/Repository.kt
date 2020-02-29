package com.nicoqueijo.android.currencyconverter.kotlin.data

import android.content.Context

class Repository(context: Context) {

    val currencyDao = CurrencyDatabase.getInstance(context).currencyDao()

    val retrofitService = RetrofitFactory.getRetrofitService()

}