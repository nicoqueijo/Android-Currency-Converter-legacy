package com.nicoqueijo.android.currencyconverter.java.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Class to manage and access the app's database using the Room library.
 */
@Database(entities = {AllCurrency.class, ActiveCurrency.class}, version = 1, exportSchema = false)
public abstract class CurrencyDatabase extends RoomDatabase {

    public abstract AllCurrencyDao getAllCurrencyDao();

    public abstract ActiveCurrencyDao getActiveCurrencyDao();

    private static final String DATABASE_NAME = "currency.db";
    private static CurrencyDatabase mInstance;

    public static synchronized CurrencyDatabase getInstance(Context context) {
        if (mInstance == null) {
            mInstance = Room.databaseBuilder(context.getApplicationContext(),
                    CurrencyDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return mInstance;
    }
}
