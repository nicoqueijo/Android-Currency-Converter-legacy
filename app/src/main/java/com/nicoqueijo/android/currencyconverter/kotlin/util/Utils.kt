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
import androidx.navigation.NavController
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.Order.INVALID
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Utility and extension functions that are used across the project.
 * @JvmStatic annotations are used so Data Binding can recognize them.
 */
object Utils {

    /**
     * Retrieves string resources using a String instead of an int.
     * Credit: https://stackoverflow.com/a/11595723/5906793
     */
    @JvmStatic
    fun getStringResourceByName(name: String, context: Context?): String {
        val resId = context!!.resources.getIdentifier(name, "string", context.packageName)
        return context.getString(resId)
    }

    /**
     * Retrieves drawable resources using a String instead of an int.
     * Credit: https://stackoverflow.com/a/11595723/5906793
     */
    @JvmStatic
    fun getDrawableResourceByName(name: String, context: Context?): Int {
        return context!!.resources.getIdentifier(name, "drawable", context.packageName)
    }

    /**
     * Used to manipulate how a resource value is set to an image view using Data Binding.
     */
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

    /**
     * If the NavController has at least two navigation entries in its backstack that means that
     * the first two are the LoadingCurrenciesFragment and the ActiveCurrenciesFragment.
     */
    fun NavController.hasActiveCurrenciesNavigation() = backStack.size > 2

    fun List<*>.hasOnlyOneElement() = size == 1

    fun List<*>.isNotLastElement(position: Int) = size > position + 1

    fun <E> List<E>.elementBefore(position: Int): E {
        if (position <= 0) {
            throw IndexOutOfBoundsException()
        }
        return this[position - 1]
    }

    fun <E> List<E>.elementAfter(position: Int): E {
        if (position >= size - 1) {
            throw IndexOutOfBoundsException()
        }
        return this[position + 1]
    }

    fun Int.isValid() = this != INVALID.position

    /**
     * Something that looks like: 0.{one or more zeros}
     * Note: decimal separator could be a comma based on locale.
     */
    fun String.isDecimaledZero() = Regex("0[.,]0+").matches(this)

    /**
     * 12,345.6789 -> 12.345,6789
     * 12.345,6789 -> 12,345.6789
     * . -> ,
     * 1. -> 1,
     *
     * x is used as a placeholder and is assumed not to be in the string which represents numbers.
     */
    fun String.swapDecimalAndGroupingSeparators(): String {
        return this.replace('.', 'x')
                .replace(',', '.')
                .replace('x', ',')
    }

    fun String.extractTrailingZeros(): String {
        var zeroCounter = 0
        run loop@{
            this.reversed().forEach {
                if (it == '0') {
                    zeroCounter++
                } else {
                    return@loop
                }
            }
        }
        return "0".repeat(zeroCounter)
    }

    enum class Order(val position: Int) {
        INVALID(-1),
        FIRST(0),
        SECOND(1),
        THIRD(2),
        FOURTH(3)
    }
}