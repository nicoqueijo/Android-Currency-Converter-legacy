package com.nicoqueijo.android.currencyconverter.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {AllCurrency.class, ActiveCurrency.class}, version = 1, exportSchema = false)
public abstract class CurrencyDatabase extends RoomDatabase {

    public abstract AllCurrencyDao getAllCurrencyDao();
    public abstract ActiveCurrencyDao getActiveCurrencyDao();

    private static final String DATABASE_NAME = "currency.db";
    private static CurrencyDatabase instance;

    public static synchronized CurrencyDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    CurrencyDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
