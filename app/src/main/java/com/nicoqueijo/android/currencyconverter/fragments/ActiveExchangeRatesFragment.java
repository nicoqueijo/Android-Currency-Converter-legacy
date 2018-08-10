package com.nicoqueijo.android.currencyconverter.fragments;

import android.content.Context;
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
import com.nicoqueijo.android.currencyconverter.helpers.Constants;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;
import com.nicoqueijo.android.currencyconverter.models.Currency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ActiveExchangeRatesFragment extends Fragment {

    public static final String TAG = ActiveExchangeRatesFragment.class.getSimpleName();

    private ArrayList<Currency> mAllCurrencies = new ArrayList<>();
    private ArrayList<Currency> mActiveCurrencies = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ActiveExchangeRatesRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFloatingActionButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        SharedPreferences mSharedPreferencesRates = getContext().getSharedPreferences(MainActivity
                .sharedPrefsRatesFilename, MODE_PRIVATE);
        Map<String, ?> keys = mSharedPreferencesRates.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            String currencyCode = entry.getKey();
            double exchangeRate = Utility.getDouble(mSharedPreferencesRates, entry.getKey(), 0.0);
            mAllCurrencies.add(new Currency(currencyCode, exchangeRate));
        }
        Collections.sort(mAllCurrencies, new Comparator<Currency>() {
            @Override
            public int compare(Currency currency1, Currency currency2) {
                return currency1.getCurrencyCode().compareTo(currency2.getCurrencyCode());
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.ARG_ACTIVE_CURRENCIES, mActiveCurrencies);
        outState.putParcelableArrayList(Constants.ARG_ALL_CURRENCIES, mAllCurrencies);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mActiveCurrencies = savedInstanceState
                    .getParcelableArrayList(Constants.ARG_ACTIVE_CURRENCIES);
            mAllCurrencies = savedInstanceState
                    .getParcelableArrayList(Constants.ARG_ALL_CURRENCIES);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_exchange_rates, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view_active_rates);
        mFloatingActionButton = view.findViewById(R.id.fab);

        mAdapter = new ActiveExchangeRatesRecyclerViewAdapter(getContext(), mActiveCurrencies);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (fragmentManager.findFragmentByTag(SelectExchangeRatesDialog.TAG) == null) {
                    DialogFragment selectExchangeRateDialog =
                            SelectExchangeRatesDialog.newInstance(mAllCurrencies);
                    selectExchangeRateDialog.show(fragmentTransaction,
                            SelectExchangeRatesDialog.TAG);
                }
            }
        });
        return view;
    }

    public static ActiveExchangeRatesFragment newInstance() {
        ActiveExchangeRatesFragment activeExchangeRatesFragment = new ActiveExchangeRatesFragment();
        return activeExchangeRatesFragment;
    }

    public void addActiveCurrency(Currency currency) {
        mActiveCurrencies.add(currency);
        mAdapter.notifyDataSetChanged();
    }

    // DO THIS AFTER I IMPLEMENT RECYCLERVIEW REORDERING (DRAGGING) BECAUSE THAT MIGHT AFFECT
    // HOW THE CURRENCIES ARE STORED.
//    private void saveActiveCurrencyToSharedPrefs() {
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences
//                (getActivity().getPackageName().concat(".active_rates"), MODE_PRIVATE);
//        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
//        for (Currency currency : mActiveCurrencies) {
//            sharedPreferencesEditor.putString(currency.getCurrencyCode(), "");
//        }
//    }
}
