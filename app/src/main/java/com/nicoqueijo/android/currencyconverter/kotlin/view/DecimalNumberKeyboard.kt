package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.nicoqueijo.android.currencyconverter.R

class DecimalNumberKeyboard(context: Context?, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

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
        View.inflate(context, R.layout.decimal_number_keyboard, this)
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
    }

}