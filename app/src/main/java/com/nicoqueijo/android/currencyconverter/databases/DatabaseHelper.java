package com.nicoqueijo.android.currencyconverter.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = DatabaseHelper.class.getSimpleName();

    public DatabaseHelper(@Nullable Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseConstants.TableAllCurrencies.CREATE_TABLE);
        db.execSQL(DatabaseConstants.TableActiveCurrencies.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
