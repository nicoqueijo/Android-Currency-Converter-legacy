package com.nicoqueijo.android.currencyconverter.kotlin.model

sealed class Resource {
    object Success : Resource()
    class Error(val message: String?) : Resource()
}