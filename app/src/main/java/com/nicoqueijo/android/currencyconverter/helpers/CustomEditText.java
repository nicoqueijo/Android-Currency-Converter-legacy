package com.nicoqueijo.android.currencyconverter.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Extension to EditText for the purpose of overriding the onSelectionChanged method.
 */
public class CustomEditText extends EditText {

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

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr,
                          int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
