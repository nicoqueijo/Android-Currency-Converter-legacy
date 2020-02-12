package com.nicoqueijo.android.currencyconverter.java.helpers;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Extension to EditText for the purpose of overriding the onSelectionChanged method.
 */
public class CustomEditText extends androidx.appcompat.widget.AppCompatEditText {

    // Required constructors
    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * This method is called when the selection has changed, in case any subclasses would like to
     * know. Overriding this so the cursor is always placed at the end when user clicks anywhere
     * within the EditText view.
     *
     * @param selStart The new selection start location.
     * @param selEnd   The new selection end location.
     */
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        setSelection(this.length());
    }
}
