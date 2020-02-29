package com.nicoqueijo.android.currencyconverter.kotlin.util

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class CustomEditText_kt : AppCompatEditText {

    // Required constructors
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    /**
     * This method is called when the selection has changed, in case any subclasses would like to
     * know. Overriding this so the cursor is always placed at the end when user clicks anywhere
     * within the EditText view.
     *
     * @param selStart The new selection start location.
     * @param selEnd   The new selection end location.
     */
    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        setSelection(length())
    }
}
