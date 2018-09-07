package com.nicoqueijo.android.currencyconverter.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.activities.MainActivity;
import com.nicoqueijo.android.currencyconverter.adapters.ActiveExchangeRatesRecyclerViewAdapter;
import com.nicoqueijo.android.currencyconverter.helpers.Constants;
import com.nicoqueijo.android.currencyconverter.helpers.SwipeAndDragHelper;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;
import com.nicoqueijo.android.currencyconverter.models.Currency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Fragment that allows the user to add/remove/reorder exchange rates and perform conversions.
 */
public class ActiveExchangeRatesFragment extends Fragment {

    public static final String TAG = ActiveExchangeRatesFragment.class.getSimpleName();

    private ArrayList<Currency> mAllCurrencies = new ArrayList<>();
    private ArrayList<Currency> mActiveCurrencies = new ArrayList<>();
    private SharedPreferences mSharedPreferencesRates;

    private RecyclerView mRecyclerView;
    private ActiveExchangeRatesRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFloatingActionButton;
    private SwipeAndDragHelper mSwipeAndDragHelper;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mSharedPreferencesRates = getContext().getSharedPreferences(MainActivity
                .sharedPrefsRatesFilename, MODE_PRIVATE);
        initAndSortCurrencies();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.ARG_ACTIVE_CURRENCIES, mActiveCurrencies);
        outState.putParcelableArrayList(Constants.ARG_ALL_CURRENCIES, mAllCurrencies);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mActiveCurrencies = savedInstanceState
                    .getParcelableArrayList(Constants.ARG_ACTIVE_CURRENCIES);
            mAllCurrencies = savedInstanceState
                    .getParcelableArrayList(Constants.ARG_ALL_CURRENCIES);
        } else {
            restoreActiveCurrenciesFromSharedPrefs();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_exchange_rates, container, false);
        initViewsAndAdapters(view);
        setUpFabOnClickListener();
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        saveActiveCurrenciesToSharedPrefs();
    }

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return a new instance of fragment
     */
    public static ActiveExchangeRatesFragment newInstance() {
        return new ActiveExchangeRatesFragment();
    }

    /**
     * Retrieves the exchange rates stored in SharedPrefs, initializes the currency list with their
     * values, and sorts the list.
     */
    private void initAndSortCurrencies() {
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

    /**
     * Initializes the views and sets up the adapters.
     *
     * @param view the root view of the inflated hierarchy
     */
    private void initViewsAndAdapters(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_active_rates);
        mFloatingActionButton = view.findViewById(R.id.fab);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ActiveExchangeRatesRecyclerViewAdapter(getContext(), mActiveCurrencies,
                mFloatingActionButton);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        mSwipeAndDragHelper = new SwipeAndDragHelper(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(mSwipeAndDragHelper);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * Sets the onClickListener for the FloatingActionButton. Dismisses the keyboard if showing.
     * Then loads up the Fragment that allows exchange rates to be selected adding it to the
     * backstack.
     */
    private void setUpFabOnClickListener() {
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (fragmentManager.findFragmentByTag(SelectExchangeRatesFragment.TAG) == null) {
                    Fragment selectExchangeRateFragment = SelectExchangeRatesFragment
                            .newInstance(mAllCurrencies);
                    fragmentTransaction.add(R.id.content_frame, selectExchangeRateFragment,
                            SelectExchangeRatesFragment.TAG);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
    }

    /**
     * Hides the keyboard if it's being shown.
     */
    private void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus()
                    .getWindowToken(), 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the newly selected currency to the data set and notifies
     * the adapter so the changes can be reflected on the UI.
     *
     * @param currency the new currency that was selected
     */
    public void addActiveCurrency(Currency currency) {
        mActiveCurrencies.add(currency);
        for (int i = 0; i < mActiveCurrencies.size(); i++) {
            mAdapter.notifyItemChanged(i);
        }
    }

    /**
     * Saves the list of active currencies to shared prefs maintaining the order in which they
     * appear. Does this by first clearing what was already inside the shared prefs to avoid
     * conflicts.
     */
    private void saveActiveCurrenciesToSharedPrefs() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences
                (getActivity().getPackageName().concat(".active_rates"), MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.clear();
        for (int i = 0; i < mActiveCurrencies.size(); i++) {
            Currency currency = mActiveCurrencies.get(i);
            sharedPreferencesEditor.putInt(currency.getCurrencyCode(), i);
        }
        sharedPreferencesEditor.apply();
    }

    /**
     * Restores the list of active currencies from shared prefs maintaining the order in which
     * they appeared.
     */
    private void restoreActiveCurrenciesFromSharedPrefs() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences
                (getActivity().getPackageName().concat(".active_rates"), MODE_PRIVATE);
        Map<String, ?> keys = sharedPreferences.getAll();
        Currency[] savedActiveCurrencies = new Currency[keys.size()];
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            String currencyCode = entry.getKey();
            double exchangeRate = Utility.getDouble(mSharedPreferencesRates, entry.getKey(), 0.0);
            int order = sharedPreferences.getInt(entry.getKey(), 0);
            Currency currency = new Currency(currencyCode, exchangeRate);
            currency = mAllCurrencies.get(mAllCurrencies.indexOf(currency));
            savedActiveCurrencies[order] = currency;
            mAllCurrencies.get(mAllCurrencies.indexOf(currency)).setSelected(true);
        }
        mActiveCurrencies.addAll(Arrays.asList(savedActiveCurrencies));
    }
}
