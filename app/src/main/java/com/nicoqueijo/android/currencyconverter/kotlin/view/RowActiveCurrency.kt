package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bosphere.fadingedgelayout.FadingEdgeLayout
import com.nicoqueijo.android.currencyconverter.R

class RowActiveCurrency(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    val rowBackground: ConstraintLayout
    val deleteIconStart: ImageView
    val deleteIconEnd: ImageView
    val rowForeground: ConstraintLayout
    val cardFlagCanvas: CardView
    val flag: ImageView
    val currencyCode: TextView
    val fadingEdgeLayout: FadingEdgeLayout
    val conversion: TextView
    val blinkingCursor: View

    init {
        LayoutInflater.from(context).inflate(R.layout.row_active_currency, this)

        rowBackground = findViewById(R.id.row_background)
        deleteIconStart = findViewById(R.id.delete_icon_start)
        deleteIconEnd = findViewById(R.id.delete_icon_end)
        rowForeground = findViewById(R.id.row_foreground)
        cardFlagCanvas = findViewById(R.id.card_flag_canvas)
        flag = findViewById(R.id.flag)
        currencyCode = findViewById(R.id.currency_code)
        fadingEdgeLayout = findViewById(R.id.fading_edge_layout)
        conversion = findViewById(R.id.conversion)
        blinkingCursor = findViewById(R.id.blinking_cursor)
    }
}