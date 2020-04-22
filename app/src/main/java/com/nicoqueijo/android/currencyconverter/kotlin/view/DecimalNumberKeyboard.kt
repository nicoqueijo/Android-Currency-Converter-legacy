package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.nicoqueijo.android.currencyconverter.R
import java.text.DecimalFormatSymbols

/**
 * Custom keyboard that includes buttons: digits 0-9, decimal separator, backspace.
 */
class DecimalNumberKeyboard(context: Context, attrs: AttributeSet) :
        ConstraintLayout(context, attrs),
        View.OnClickListener,
        View.OnLongClickListener {

    private var onClickCallback: KeyboardCallback? = null
    private var onLongClickCallback: KeyboardCallback? = null

    private val buttonOne: Button
    private val buttonTwo: Button
    private val buttonThree: Button
    private val buttonFour: Button
    private val buttonFive: Button
    private val buttonSix: Button
    private val buttonSeven: Button
    private val buttonEight: Button
    private val buttonNine: Button
    private val buttonDecimalSeparator: Button
    private val buttonZero: Button
    private val buttonBackspace: ImageButton

    init {
        LayoutInflater.from(context).inflate(R.layout.decimal_number_keyboard, this)
        buttonOne = findViewById(R.id.button_one)
        buttonTwo = findViewById(R.id.button_two)
        buttonThree = findViewById(R.id.button_three)
        buttonFour = findViewById(R.id.button_four)
        buttonFive = findViewById(R.id.button_five)
        buttonSix = findViewById(R.id.button_six)
        buttonSeven = findViewById(R.id.button_seven)
        buttonEight = findViewById(R.id.button_eight)
        buttonNine = findViewById(R.id.button_nine)
        buttonDecimalSeparator = findViewById(R.id.button_decimal_separator)
        buttonZero = findViewById(R.id.button_zero)
        buttonBackspace = findViewById(R.id.button_backspace)
        buttonDecimalSeparator.text = DecimalFormatSymbols.getInstance().decimalSeparator.toString()
        setListeners(buttonOne, buttonTwo, buttonThree, buttonFour, buttonFive,
                buttonSix, buttonSeven, buttonEight, buttonNine, buttonDecimalSeparator,
                buttonZero, buttonBackspace)
    }

    private fun setListeners(vararg buttons: View) {
        buttons.forEach { button ->
            button.setOnClickListener(this)
            if (button is ImageButton) {
                button.setOnLongClickListener(this)
            }
        }
    }

    fun onKeyClickedListener(listener: KeyboardCallback) {
        onClickCallback = listener
    }

    fun onKeyLongClickedListener(listener: KeyboardCallback) {
        onLongClickCallback = listener
    }

    override fun onClick(button: View?) {
        onClickCallback?.invoke(button)
    }

    override fun onLongClick(button: View?): Boolean {
        onLongClickCallback?.invoke(button)
        return true
    }
}

/**
 * Credit: https://stackoverflow.com/a/60906741/5906793
 */
typealias KeyboardCallback = (View?) -> Unit