package com.nicoqueijo.android.currencyconverter.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.Lists;
import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.adapters.ActiveCurrenciesAdapter;
import com.nicoqueijo.android.currencyconverter.databases.DatabaseContract.EntryActiveCurrencies;
import com.nicoqueijo.android.currencyconverter.databases.DatabaseContract.EntryAllCurrencies;
import com.nicoqueijo.android.currencyconverter.databases.DatabaseHelper;
import com.nicoqueijo.android.currencyconverter.helpers.CustomRecyclerView;
import com.nicoqueijo.android.currencyconverter.helpers.SwipeAndDragHelper;
import com.nicoqueijo.android.currencyconverter.models.Currency;
import com.nicoqueijo.android.currencyconverter.singletons.CurrenciesSingleton;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Fragment that allows the user to add/remove/reorder exchange rates and perform conversions.
 */
public class ActiveCurrenciesFragment extends Fragment {

    public static final String TAG = ActiveCurrenciesFragment.class.getSimpleName();
    public static final String ARG_ALL_CURRENCIES = "arg_all_currencies";
    public static final String ARG_ACTIVE_CURRENCIES = "arg_active_currencies";

    private int fabClicks = 0;
    private ArrayList<Currency> mAllCurrencies;
    private ArrayList<Currency> mActiveCurrencies = Lists.newArrayList();

    private InterstitialAd mInterstitialAd;
    private CustomRecyclerView mRecyclerView;
    private View mEmptyListView;
    private ActiveCurrenciesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFloatingActionButton;

    /**
     * Factory method to create a new instance of this Fragment using the provided parameters.
     *
     * @return a new instance of Fragment
     */
    public static ActiveCurrenciesFragment newInstance() {
        return new ActiveCurrenciesFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAllCurrencies = CurrenciesSingleton.getInstance(getContext()).getCurrencies();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ARG_ACTIVE_CURRENCIES, mActiveCurrencies);
        outState.putParcelableArrayList(ARG_ALL_CURRENCIES, mAllCurrencies);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mActiveCurrencies = savedInstanceState.getParcelableArrayList(ARG_ACTIVE_CURRENCIES);
            mAllCurrencies = savedInstanceState.getParcelableArrayList(ARG_ALL_CURRENCIES);
        } else {
            restoreActiveCurrencies();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_currencies, container, false);
        initInterstitialAds();
        initViewsAndAdapters(view);
        setUpFabOnClickListener();
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        persistActiveCurrencies();
    }

    /**
     * Initializes the interstitial ad configurations.
     */
    private void initInterstitialAds() {
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.ad_unit_id_interstitial_test));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    /**
     * Initializes the views and sets up the adapters.
     *
     * @param view the root view of the inflated hierarchy
     */
    private void initViewsAndAdapters(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_active_currencies);
        mEmptyListView = view.findViewById(R.id.container_empty_list);
        mRecyclerView.showIfEmpty(mEmptyListView);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mFloatingActionButton = view.findViewById(R.id.floating_action_button);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ActiveCurrenciesAdapter(getContext(), mActiveCurrencies,
                mFloatingActionButton);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new SwipeAndDragHelper(mAdapter,
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
    }

    /**
     * Sets the onClickListener for the FloatingActionButton. Determines whether to show an
     * interstitial ad, dismisses the keyboard and snackbar if showing, and then loads up the
     * Fragment that allows exchange rates to be selected adding it to the backstack.
     */
    private void setUpFabOnClickListener() {
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processInterstitialAd();
                hideKeyboard();
                mAdapter.dismissSnackbar();
                addFragment();
            }
        });
    }

    /**
     * Adds the SelectableCurrenciesFragment to enable the user to add a currency to the set of
     * active currencies.
     */
    private void addFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.findFragmentByTag(SelectableCurrenciesFragment.TAG) == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right, android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right);
            Fragment selectableCurrencyFragment = SelectableCurrenciesFragment.newInstance();
            fragmentTransaction.add(R.id.content_frame, selectableCurrencyFragment,
                    SelectableCurrenciesFragment.TAG);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    /**
     * Shows an interstitial ad every 4 clicks of the FloatingActionButton granted the ad has loaded.
     */
    private void processInterstitialAd() {
        fabClicks++;
        if (fabClicks % 4 == 0) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }

    /**
     * Hides the keyboard if it's being shown.
     */
    private void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);
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
     * Saves the list of active currencies to an SQLite database table maintaining the order in
     * which they appear. Does this by first deleting everything from the table to avoid conflicts.
     */
    private void persistActiveCurrencies() {
        try {
            DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
            SQLiteDatabase database = databaseHelper.getWritableDatabase();
            database.beginTransaction();
            database.execSQL("DELETE FROM " + EntryActiveCurrencies.TABLE_NAME);
            ContentValues contentValues = new ContentValues();
            for (int i = 0; i < mActiveCurrencies.size(); i++) {
                String currencyCode = mActiveCurrencies.get(i).getCurrencyCode();
                contentValues.put(EntryActiveCurrencies.COLUMN_CURRENCY_ORDER, i);
                contentValues.put(EntryActiveCurrencies.COLUMN_CURRENCY_CODE, currencyCode);
                database.insert(EntryActiveCurrencies.TABLE_NAME, null, contentValues);
            }
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restores the list of active currencies from the SQLite database maintaining the order in
     * which they appeared. Does this by querying the active_currencies database table and loading
     * its content onto the array that will serve as the adapter's data set.
     * Uses rawQuery instead of query to make an inner join because the currency code column is a
     * foreign key.
     */
    private void restoreActiveCurrencies() {
        try {
            DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            database.beginTransaction();
            String rawQuery = "SELECT" +
                    " active_currencies.currency_order," +
                    " active_currencies.currency_code," +
                    " all_currencies.currency_value" +
                    " FROM active_currencies" +
                    " INNER JOIN all_currencies" +
                    " WHERE all_currencies.currency_code =  active_currencies.currency_code";
            Cursor cursor = database.rawQuery(rawQuery, null);
            Currency[] savedActiveCurrencies = new Currency[cursor.getCount()];
            int col_currency_order = cursor.getColumnIndex(EntryActiveCurrencies
                    .COLUMN_CURRENCY_ORDER);
            int col_currency_code = cursor.getColumnIndex(EntryActiveCurrencies
                    .COLUMN_CURRENCY_CODE);
            int col_currency_value = cursor.getColumnIndex(EntryAllCurrencies
                    .COLUMN_CURRENCY_VALUE);
            while (cursor.moveToNext()) {
                int order = cursor.getInt(col_currency_order);
                String currencyCode = cursor.getString(col_currency_code);
                double exchangeRate = cursor.getDouble(col_currency_value);
                Currency currency = new Currency(currencyCode, exchangeRate);
                currency = mAllCurrencies.get(mAllCurrencies.indexOf(currency));
                savedActiveCurrencies[order] = currency;
                mAllCurrencies.get(mAllCurrencies.indexOf(currency)).setSelected(true);
            }
            mActiveCurrencies.addAll(Arrays.asList(savedActiveCurrencies));
            cursor.close();
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }
}
