package com.nicoqueijo.android.currencyconverter.kotlin.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.DialogFragment
import java.math.BigDecimal
import java.math.RoundingMode

object Utils {
    @JvmStatic
    fun getStringResourceByName(name: String, context: Context?): String {
        val resId = context!!.resources.getIdentifier(name, "string", context.packageName)
        return context.getString(resId)
    }

    @JvmStatic
    fun getDrawableResourceByName(name: String, context: Context?): Int {
        return context!!.resources.getIdentifier(name, "drawable", context.packageName)
    }

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageViewResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }

    @JvmStatic
    fun putDouble(edit: SharedPreferences.Editor, key: String, value: Double) {
        edit.putLong(key, java.lang.Double.doubleToRawLongBits(value))
    }

    @JvmStatic
    fun getDouble(prefs: SharedPreferences, key: String, defaultValue: Double): Double {
        return java.lang.Double.longBitsToDouble(prefs.getLong(key, java.lang.Double.doubleToLongBits(defaultValue)))
    }

    @JvmStatic
    fun roundBigDecimal(value: BigDecimal): BigDecimal {
        val decimalPlaces = 2
        return value.setScale(decimalPlaces, RoundingMode.CEILING)
    }

    @JvmStatic
    fun roundDialogCorners(dialogFragment: DialogFragment) {
        dialogFragment.dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun hideKeyboard(activity: Activity?) {
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
    }
}