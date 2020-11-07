package com.nicoqueijo.android.currencyconverter.kotlin.data

import android.content.Context

class AppPrefs(context: Context) {

    private val sharedPrefs = context.getSharedPreferences("${context.packageName}.properties",
            Context.MODE_PRIVATE)
    private val editor = sharedPrefs.edit()

    var isFirstLaunch: Boolean
        get() = sharedPrefs.getBoolean(FIRST_LAUNCH, true)
        set(value) = editor.putBoolean(FIRST_LAUNCH, value).apply()

    var timestamp: Long
        get() = (sharedPrefs.getLong(TIMESTAMP, NO_DATA))
        set(value) = editor.putLong(TIMESTAMP, value).apply()

    val isDataStale
        get() = timeSinceLastUpdate > TWENTY_FOUR_HOURS

    val isDataEmpty
        get() = timeSinceLastUpdate == NO_DATA

    private val timeSinceLastUpdate: Long
        get() {
            return if (timestamp != NO_DATA) {
                System.currentTimeMillis() - (timestamp * 1000L)
            } else {
                NO_DATA
            }
        }

    companion object {
        const val FIRST_LAUNCH = "first_launch"
        const val TIMESTAMP = "timestamp"
        const val TWENTY_FOUR_HOURS = 86400000L
        const val NO_DATA = 0L
    }
}