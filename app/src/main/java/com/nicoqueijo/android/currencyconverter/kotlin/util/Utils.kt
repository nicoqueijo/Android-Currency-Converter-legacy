package com.nicoqueijo.android.currencyconverter.kotlin.util

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.DialogFragment
import java.math.BigDecimal
import java.math.RoundingMode

object Utils {

    fun getStringResourceByName(name: String, context: Context?): String {
        val resId = context!!.resources.getIdentifier(name, "string", context.packageName)
        return context.getString(resId)
    }

    fun getDrawableResourceByName(name: String, context: Context?): Int {
        return context!!.resources.getIdentifier(name, "drawable", context.packageName)
    }

    @BindingAdapter("android:src")
    fun setImageViewResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }

    fun putDouble(edit: SharedPreferences.Editor, key: String, value: Double) {
        edit.putLong(key, java.lang.Double.doubleToRawLongBits(value))
    }

    fun getDouble(prefs: SharedPreferences, key: String, defaultValue: Double): Double {
        return java.lang.Double.longBitsToDouble(prefs.getLong(key, java.lang.Double.doubleToLongBits(defaultValue)))
    }

    fun roundBigDecimal(value: BigDecimal): BigDecimal {
        val decimalPlaces = 2
        return value.setScale(decimalPlaces, RoundingMode.CEILING)
    }

    fun roundDialogCorners(dialogFragment: DialogFragment) {
        dialogFragment.dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}