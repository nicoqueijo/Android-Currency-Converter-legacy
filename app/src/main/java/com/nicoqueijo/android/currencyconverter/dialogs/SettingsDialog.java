package com.nicoqueijo.android.currencyconverter.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.nicoqueijo.android.currencyconverter.R;

import java.util.Stack;

/**
 * Groups common fields and methods that both the Language and Theme Dialogs use in an attempt to
 * promote code reusability.
 */
public abstract class SettingsDialog extends DialogFragment {

    public static final String TAG = SettingsDialog.class.getSimpleName();

    SharedPreferences mSharedPrefsProperties;
    Stack<RadioButton> mActiveRadioButton = new Stack<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefsProperties = getActivity().getSharedPreferences(getActivity().getPackageName()
                .concat(".properties"), Context.MODE_PRIVATE);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setWindowAnimations(R.style.dialog_animation_slide);
    }

    public abstract void initViews(View view);

}
