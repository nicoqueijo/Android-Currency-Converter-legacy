package com.nicoqueijo.android.currencyconverter.singletons;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.nicoqueijo.android.currencyconverter.databases.DatabaseContract.EntryAllCurrencies;
import com.nicoqueijo.android.currencyconverter.databases.DatabaseHelper;
import com.nicoqueijo.android.currencyconverter.models.Currency;

import java.util.ArrayList;

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
     * Retrieves the exchange rates stored in the SQLite database and initializes the currency list
     * with their values
     *
     * @param context Information about application environment. Needed to open database.
     */
    private void initAndSortCurrencies(Context context) {
        try {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            database.beginTransaction();
            Cursor cursor = database.query(
                    EntryAllCurrencies.TABLE_NAME,
                    new String[]{
                            EntryAllCurrencies.COLUMN_CURRENCY_CODE,
                            EntryAllCurrencies.COLUMN_CURRENCY_VALUE},
                    null,
                    null,
                    null,
                    null,
                    EntryAllCurrencies.COLUMN_CURRENCY_CODE + " ASC");
            int col_currency_code = cursor.getColumnIndex(EntryAllCurrencies.COLUMN_CURRENCY_CODE);
            int col_currency_value = cursor.getColumnIndex(EntryAllCurrencies.COLUMN_CURRENCY_VALUE);
            String currencyCode;
            double exchangeRate;
            while (cursor.moveToNext()) {
                currencyCode = cursor.getString(col_currency_code);
                exchangeRate = cursor.getDouble(col_currency_value);
                mCurrencies.add(new Currency(currencyCode, exchangeRate));
            }
            cursor.close();
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }
}
