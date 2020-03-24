package com.nicoqueijo.android.currencyconverter.kotlin.util

import android.app.Activity
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.DialogFragment
import java.math.BigDecimal
import java.math.RoundingMode

// @JvmStatic annotations so Data Binding can recognize them
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
        val decimalPlaces = 4
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

    // Vibrate for 10 milliseconds
    fun vibrate(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (context.getSystemService(VIBRATOR_SERVICE) as Vibrator)
                    .vibrate(VibrationEffect.createOneShot(10L, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            (context.getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(25L)
        }
    }
}