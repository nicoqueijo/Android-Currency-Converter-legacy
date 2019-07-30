package com.nicoqueijo.android.currencyconverter.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nicoqueijo.android.currencyconverter.R;

/**
 * Fragment to let the user know the exchange rate data is being fetched from the internet.
 */
public class LoadingCurrenciesFragment extends Fragment {

    public static final String TAG = LoadingCurrenciesFragment.class.getSimpleName();

    /**
     * Factory method to create a new instance of this Fragment using the provided parameters.
     *
     * @return a new instance of Fragment
     */
    public static LoadingCurrenciesFragment newInstance() {
        return new LoadingCurrenciesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loading_currencies, container, false);
    }
}
