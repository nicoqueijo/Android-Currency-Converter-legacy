package com.nicoqueijo.android.currencyconverter.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Extension to EditText for the purpose of overriding the onSelectionChanged method so the cursor
 * is always placed in the end when selected or pressed on.
 */
public class BlockSelectionEditText extends EditText {
    public BlockSelectionEditText(Context context) {
        super(context);
    }

    public BlockSelectionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlockSelectionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BlockSelectionEditText(Context context, AttributeSet attrs, int defStyleAttr,
                                  int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        setSelection(this.length());
    }

//    @Override
//    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_DEL) {
//            this.setText("");
//            return true;
//        }
//        return super.onKeyLongPress(keyCode, event);
//    }
}
