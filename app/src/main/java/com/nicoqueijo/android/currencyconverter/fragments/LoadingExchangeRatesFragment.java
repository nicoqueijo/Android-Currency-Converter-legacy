package com.nicoqueijo.android.currencyconverter.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nicoqueijo.android.currencyconverter.R;

/**
 * Fragment to let the user know the exchange rate data is being fetched from the internet.
 */
public class LoadingExchangeRatesFragment extends Fragment {

    public static final String TAG = LoadingExchangeRatesFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loading_exchange_rates, container, false);
    }

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return a new instance of fragment
     */
    public static LoadingExchangeRatesFragment newInstance() {
        LoadingExchangeRatesFragment loadingExchangeRatesFragment = new LoadingExchangeRatesFragment();
        Bundle args = new Bundle();
        return loadingExchangeRatesFragment;
    }

}
