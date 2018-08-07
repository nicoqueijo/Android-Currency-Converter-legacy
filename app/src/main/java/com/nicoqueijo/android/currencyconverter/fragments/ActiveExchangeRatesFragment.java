package com.nicoqueijo.android.currencyconverter.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.activities.MainActivity;
import com.nicoqueijo.android.currencyconverter.adapters.ActiveExchangeRatesRecyclerViewAdapter;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;
import com.nicoqueijo.android.currencyconverter.models.Currency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ActiveExchangeRatesFragment extends Fragment {

    public static final String TAG = ActiveExchangeRatesFragment.class.getSimpleName();

    private ArrayList<Currency> mCurrencies = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFloatingActionButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_exchange_rates, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view_active_rates);
        mFloatingActionButton = view.findViewById(R.id.fab);

        SharedPreferences mSharedPreferencesRates = getContext().getSharedPreferences(MainActivity
                .sharedPrefsRatesFilename, MODE_PRIVATE);

        Map<String, ?> keys = mSharedPreferencesRates.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            mCurrencies.add(new Currency(entry.getKey(),
                    Utility.getDouble(mSharedPreferencesRates, entry.getKey(), 0.0)));
        }
        Collections.sort(mCurrencies, new Comparator<Currency>() {
            @Override
            public int compare(Currency currency1, Currency currency2) {
                return currency1.getCurrencyCode().compareTo(currency2.getCurrencyCode());
            }
        });

        mAdapter = new ActiveExchangeRatesRecyclerViewAdapter(getContext(), mCurrencies);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Open dialog fragment containing list of all possible mCurrencies
                // This dialog fragment should contain a SearchView on top and a
                // RecyclerView below it. Currencies that are already in the
                // ActiveExchangeRatesFragment should not be contenders for selection.
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                DialogFragment selectExchangeRateDialog =
                        SelectExchangeRatesDialog.newInstance(mCurrencies);
                selectExchangeRateDialog.show(fragmentTransaction, TAG);
            }
        });

        return view;
    }

    public static ActiveExchangeRatesFragment newInstance() {
        ActiveExchangeRatesFragment activeExchangeRatesFragment = new ActiveExchangeRatesFragment();
        Bundle args = new Bundle();
        return activeExchangeRatesFragment;
    }
}
