package com.nicoqueijo.android.currencyconverter.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.activities.MainActivity;
import com.nicoqueijo.android.currencyconverter.adapters.ActiveCurrenciesAdapter;
import com.nicoqueijo.android.currencyconverter.databases.DatabaseConstants.TableActiveCurrencies;
import com.nicoqueijo.android.currencyconverter.databases.DatabaseHelper;
import com.nicoqueijo.android.currencyconverter.helpers.CustomRecyclerView;
import com.nicoqueijo.android.currencyconverter.helpers.SwipeAndDragHelper;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;
import com.nicoqueijo.android.currencyconverter.models.Currency;
import com.nicoqueijo.android.currencyconverter.singletons.CurrenciesSingleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Fragment that allows the user to add/remove/reorder exchange rates and perform conversions.
 */
public class ActiveCurrenciesFragment extends Fragment {

    public static final String TAG = ActiveCurrenciesFragment.class.getSimpleName();
    public static final String ARG_ALL_CURRENCIES = "arg_all_currencies";
    public static final String ARG_ACTIVE_CURRENCIES = "arg_active_currencies";

    private ArrayList<Currency> mAllCurrencies;
    private ArrayList<Currency> mActiveCurrencies = new ArrayList<>();
    private SharedPreferences mSharedPrefsRates;

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
        mSharedPrefsRates = getContext().getSharedPreferences(MainActivity
                .sharedPrefsRatesFilename, MODE_PRIVATE);
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
            restoreActiveCurrenciesFromSharedPrefs();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_currencies, container, false);
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
                if (fragmentManager.findFragmentByTag(SelectCurrenciesFragment.TAG) == null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right, android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right);
                    Fragment selectCurrencyFragment = SelectCurrenciesFragment.newInstance();
                    fragmentTransaction.add(R.id.content_frame, selectCurrencyFragment,
                            SelectCurrenciesFragment.TAG);
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
     * Saves the list of active currencies to shared prefs maintaining the order in which they
     * appear. Does this by first clearing what was already inside the shared prefs to avoid
     * conflicts.
     */
    private void saveActiveCurrenciesToSharedPrefs() {
//        SharedPreferences sharedPrefsActiveRates = getActivity().getSharedPreferences
//                (getActivity().getPackageName().concat(".active_rates"), MODE_PRIVATE);
//        SharedPreferences.Editor sharedPrefsEditor = sharedPrefsActiveRates.edit();
//        sharedPrefsEditor.clear();
//        for (int i = 0; i < mActiveCurrencies.size(); i++) {
//            Currency currency = mActiveCurrencies.get(i);
//            sharedPrefsEditor.putInt(currency.getCurrencyCode(), i);
//        }
//        sharedPrefsEditor.apply();

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        database.beginTransaction();
        database.execSQL("DELETE FROM " + TableActiveCurrencies.TABLE_NAME);
        ContentValues contentValues = new ContentValues();
        String currencyCode;
        for (int i = 0; i < mActiveCurrencies.size(); i++) {
            currencyCode = mActiveCurrencies.get(i).getCurrencyCode();
            contentValues.put(TableActiveCurrencies.COLUMN_ORDER, i);
            contentValues.put(TableActiveCurrencies.COLUMN_CURRENCY_CODE, currencyCode);
            database.insert(TableActiveCurrencies.TABLE_NAME, null, contentValues);
//            Currency currency = mActiveCurrencies.get(i);
//            sharedPrefsEditor.putInt(currency.getCurrencyCode(), i);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }

    /**
     * Restores the list of active currencies from shared prefs maintaining the order in which
     * they appeared.
     */
    private void restoreActiveCurrenciesFromSharedPrefs() {
        SharedPreferences sharedPrefsActiveRates = getActivity().getSharedPreferences
                (getActivity().getPackageName().concat(".active_rates"), MODE_PRIVATE);
        Map<String, ?> keys = sharedPrefsActiveRates.getAll();
        Currency[] savedActiveCurrencies = new Currency[keys.size()];
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            String currencyCode = entry.getKey();
            double exchangeRate = Utility.getDouble(mSharedPrefsRates, entry.getKey(), 0.0);
            int order = sharedPrefsActiveRates.getInt(entry.getKey(), 0);
            Currency currency = new Currency(currencyCode, exchangeRate);
            currency = mAllCurrencies.get(mAllCurrencies.indexOf(currency));
            savedActiveCurrencies[order] = currency;
            mAllCurrencies.get(mAllCurrencies.indexOf(currency)).setSelected(true);
        }
        mActiveCurrencies.addAll(Arrays.asList(savedActiveCurrencies));
    }
}
