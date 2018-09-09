package com.nicoqueijo.android.currencyconverter.dialogs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.nicoqueijo.android.currencyconverter.R;

import java.util.Stack;

public class ThemeDialog extends DialogFragment {

    public enum Theme {
        dark,
        light
    }

    public static final String TAG = ThemeDialog.class.getSimpleName();

    private SharedPreferences mSharedPreferences;
    private Stack<RadioButton> mActiveRadioButton = new Stack<>();
    private LinearLayout mLightOption;
    private LinearLayout mDarkOption;
    private RadioButton mLightRadioButton;
    private RadioButton mDarkRadioButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_theme, container, false);



        return view;
    }

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return a new instance of fragment
     */
    public static ThemeDialog newInstance() {
        return new ThemeDialog();
    }

}
