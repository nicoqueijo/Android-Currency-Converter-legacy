package com.nicoqueijo.android.currencyconverter.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;

/**
 * Groups common fields and methods that both the Language and Theme Dialogs use in an attempt to
 * promote code reusability.
 */
public abstract class SettingsDialog extends DialogFragment {

    public static final String TAG = SettingsDialog.class.getSimpleName();

    public SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName()
                .concat(".settings"), Context.MODE_PRIVATE);
    }

    public abstract void initViews(View view);

}
