package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.nicoqueijo.android.currencyconverter.R

class RowActiveCurrency(context: Context?, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    val flag: ImageView
    val currencyCode: TextView
    val conversion: TextView
    val blinkingCursor: View

    init {
        LayoutInflater.from(context).inflate(R.layout.row_active_currency, this)
        flag = findViewById(R.id.flag)
        currencyCode = findViewById(R.id.currency_code)
        conversion = findViewById(R.id.conversion)
        blinkingCursor = findViewById(R.id.blinking_cursor)
    }

}