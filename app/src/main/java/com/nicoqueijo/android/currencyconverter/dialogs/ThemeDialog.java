package com.nicoqueijo.android.currencyconverter.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nicoqueijo.android.currencyconverter.R;

public class ThemeDialog extends DialogFragment {

    public static final String TAG = ThemeDialog.class.getSimpleName();

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
