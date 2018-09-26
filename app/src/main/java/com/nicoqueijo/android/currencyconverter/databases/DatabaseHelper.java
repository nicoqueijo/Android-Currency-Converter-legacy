package com.nicoqueijo.android.currencyconverter.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = DatabaseHelper.class.getSimpleName();

    public DatabaseHelper(@Nullable Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.VERSION_NUMBER);
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
