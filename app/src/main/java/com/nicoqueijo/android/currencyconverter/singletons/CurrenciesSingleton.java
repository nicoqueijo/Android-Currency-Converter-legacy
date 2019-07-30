package com.nicoqueijo.android.currencyconverter.singletons;

import android.content.Context;

import com.google.common.collect.Lists;
import com.nicoqueijo.android.currencyconverter.models.Currency;
import com.nicoqueijo.android.currencyconverter.room.AllCurrency;
import com.nicoqueijo.android.currencyconverter.room.AllCurrencyDao;
import com.nicoqueijo.android.currencyconverter.room.CurrencyDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class for the main data set of the app which is the list of currencies along with
 * their exchange rate values so we can use the same ArrayList<Currency> instance throughout the
 * whole lifetime of the app instead of passing the object throughout Fragments.
 */
public class CurrenciesSingleton {

    public static final String TAG = CurrenciesSingleton.class.getSimpleName();

    private static CurrenciesSingleton mInstance;
    private ArrayList<Currency> mCurrencies = Lists.newArrayList();

    private CurrenciesSingleton(Context context) {
        retrieveAndInitCurrencies(context);
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
     * Retrieves the exchange rates stored in the SQLite database and initializes the currency list
     * with their values.
     *
     * @param context Information about application environment. Needed to open database.
     */
    private void retrieveAndInitCurrencies(Context context) {
        CurrencyDatabase currencyDatabase = CurrencyDatabase.getInstance(context);
        AllCurrencyDao allCurrencyDao = currencyDatabase.getAllCurrencyDao();
        List<AllCurrency> allCurrencies = allCurrencyDao.getAllCurrencies();
        for (AllCurrency allCurrency : allCurrencies) {
            mCurrencies.add(new Currency(allCurrency.getCurrencyCode(), allCurrency.getCurrencyValue()));
        }
    }
}
