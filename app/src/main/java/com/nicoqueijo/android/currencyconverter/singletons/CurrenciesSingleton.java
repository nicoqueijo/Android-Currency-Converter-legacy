package com.nicoqueijo.android.currencyconverter.singletons;

import android.content.Context;
import android.content.SharedPreferences;

import com.nicoqueijo.android.currencyconverter.activities.MainActivity;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;
import com.nicoqueijo.android.currencyconverter.models.Currency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Singleton class for the main data set of the app which is the list of currencies along with
 * their exchange rate values so we can use the same ArrayList<Currency> instance throughout the
 * whole lifetime of the app instead of passing the object throughout Fragments.
 */
public class CurrenciesSingleton {

    public static final String TAG = CurrenciesSingleton.class.getSimpleName();

    private static CurrenciesSingleton mInstance;
    private ArrayList<Currency> mCurrencies = new ArrayList<>();

    private CurrenciesSingleton(Context context) {
        initAndSortCurrencies(context);
    }

    public static synchronized CurrenciesSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CurrenciesSingleton(context);
        }
        return mInstance;
    }

    public ArrayList<Currency> getCurrencies() {
        return mCurrencies;
    }

    /**
     * /////////////////////////////// EDIT THIS /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     * Retrieves the exchange rates stored in SharedPrefs, initializes the currency list with their
     * values, and sorts the list.
     *
     * @param context Information about application environment. Needed to get SharedPrefs.
     */
    private void initAndSortCurrencies(Context context) {
        SharedPreferences mSharedPrefsRates = context.getSharedPreferences(MainActivity
                .sharedPrefsRatesFilename, MODE_PRIVATE);
        Map<String, ?> keys = mSharedPrefsRates.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            String currencyCode = entry.getKey();
            double exchangeRate = Utility.getDouble(mSharedPrefsRates, entry.getKey(), 0.0);
            mCurrencies.add(new Currency(currencyCode, exchangeRate));
        }
        Collections.sort(mCurrencies, new Comparator<Currency>() {
            @Override
            public int compare(Currency currency1, Currency currency2) {
                return currency1.getCurrencyCode().compareTo(currency2.getCurrencyCode());
            }
        });
    }
}
