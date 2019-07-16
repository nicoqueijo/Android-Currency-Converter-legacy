package com.nicoqueijo.android.currencyconverter.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = DatabaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "currencies.db";
    private static final int VERSION_NUMBER = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.EntryAllCurrencies.CREATE_TABLE);
        db.execSQL(DatabaseContract.EntryActiveCurrencies.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
