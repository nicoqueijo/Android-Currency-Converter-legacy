package com.nicoqueijo.android.currencyconverter.kotlin.util

import android.app.Activity
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @JvmStatic annotations so Data Binding can recognize them
 */
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
    fun setImageViewResource(imageView: ImageView, resource: Int) = imageView.setImageResource(resource)

    fun BigDecimal.roundToFourDecimalPlaces(): BigDecimal = setScale(4, RoundingMode.CEILING)

    fun Activity.hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun Context.vibrate() {
        val duration = 10L
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (getSystemService(VIBRATOR_SERVICE) as Vibrator)
                    .vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(duration)
        }
    }

    fun List<*>.hasMoreThanOneElement() = size > 1

    fun Int.isValid() = this != Order.INVALID.position

    enum class Order(val position: Int) {
        INVALID(-1),
        FIRST(0),
        SECOND(1),
        THIRD(2),
        FOURTH(3)
    }
}