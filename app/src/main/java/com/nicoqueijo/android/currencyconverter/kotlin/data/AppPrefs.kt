package com.nicoqueijo.android.currencyconverter.kotlin.data

import android.content.Context
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.toMillis

class AppPrefs(context: Context) {

    private val sharedPrefs = context.getSharedPreferences("${context.packageName}.properties",
            Context.MODE_PRIVATE)
    private val editor = sharedPrefs.edit()

    var isFirstLaunch: Boolean
        get() = sharedPrefs.getBoolean(FIRST_LAUNCH, true)
        set(value) = editor.putBoolean(FIRST_LAUNCH, value).apply()

    var timestampInSeconds: Long
        get() = (sharedPrefs.getLong(TIMESTAMP, NO_DATA))
        set(value) = editor.putLong(TIMESTAMP, value).apply()

    val isDataStale
        get() = timeSinceLastUpdateInMillis > TWENTY_FOUR_HOURS_IN_MILLIS

    val isDataEmpty
        get() = timeSinceLastUpdateInMillis == NO_DATA

    private val timeSinceLastUpdateInMillis: Long
        get() {
            return if (timestampInSeconds != NO_DATA) {
                System.currentTimeMillis() - timestampInSeconds.toMillis()
            } else {
                NO_DATA
            }
        }

    companion object {
        const val FIRST_LAUNCH = "first_launch"
        const val TIMESTAMP = "timestamp"
        const val TWENTY_FOUR_HOURS_IN_MILLIS = 86_400_000L
        const val NO_DATA = 0L
    }
}