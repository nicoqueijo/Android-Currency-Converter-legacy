package com.nicoqueijo.android.currencyconverter.kotlin.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.graphics.Rect
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.view.children
import androidx.databinding.BindingAdapter
import com.jmedeisis.draglinearlayout.DragLinearLayout
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.Order.INVALID
import com.nicoqueijo.android.currencyconverter.kotlin.view.RowActiveCurrency
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Utility and extension functions that are used across the project.
 * @JvmStatic annotations are used so Data Binding can recognize them.
 */
object Utils {

    enum class Order(val position: Int) {
        INVALID(-1),
        FIRST(0),
        SECOND(1),
        THIRD(2),
        FOURTH(3)
    }

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

    fun Context.copyToClipboard(text: CharSequence) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, getString(R.string.conversion_copied), Toast.LENGTH_SHORT).show()
    }

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

    fun View.show() {
        visibility = View.VISIBLE
    }

    fun View.hide() {
        visibility = View.GONE
    }

    fun MenuItem.show() {
        isVisible = true
    }

    fun MenuItem.hide() {
        isVisible = false
    }

    /**
     * Can't extend the toString() method because it will get shadowed since it already exists.
     * This is the next best thing.
     */
    fun DragLinearLayout.asString() =
            children.joinToString(prefix = "{ ", postfix = " }") {
                (it as RowActiveCurrency).toString()
            }

    /**
     * Credit: https://stackoverflow.com/a/55487608/5906793
     */
    fun ScrollView.isViewVisible(view: View): Boolean {
        val scrollBounds = Rect()
        this.getDrawingRect(scrollBounds)
        var top = 0f
        var temp = view
        while (temp !is ScrollView) {
            top += (temp).y
            temp = temp.parent as View
        }
        val bottom = top + view.height
        return scrollBounds.top < top && scrollBounds.bottom > bottom
    }

    fun BigDecimal.roundToFourDecimalPlaces(): BigDecimal = setScale(4, RoundingMode.CEILING)

    fun Int.isValid() = this != INVALID.position
}