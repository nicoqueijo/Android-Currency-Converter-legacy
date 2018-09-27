package com.nicoqueijo.android.currencyconverter.databases;

public class DatabaseContract {

    public static final String TAG = DatabaseContract.class.getSimpleName();

    public class EntryAllCurrencies {

        public static final String TABLE_NAME = "all_currencies";
        public static final String COLUMN_CURRENCY_CODE = "currency_code";
        public static final String COLUMN_CURRENCY_VALUE = "currency_value";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
                + " ("
                + COLUMN_CURRENCY_CODE + " TEXT PRIMARY KEY, "
                + COLUMN_CURRENCY_VALUE + " REAL"
                + ");";
    }

    public class EntryActiveCurrencies {

        public static final String TABLE_NAME = "active_currencies";
        public static final String COLUMN_CURRENCY_ORDER = "currency_order";
        public static final String COLUMN_CURRENCY_CODE = "currency_code";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
                + " ("
                + COLUMN_CURRENCY_ORDER + " INTEGER PRIMARY KEY, "
                + COLUMN_CURRENCY_CODE + " TEXT, "
                + "FOREIGN KEY (" + COLUMN_CURRENCY_CODE + ") REFERENCES "
                + EntryAllCurrencies.TABLE_NAME
                + "(" + EntryAllCurrencies.COLUMN_CURRENCY_CODE + ")" + ");";
    }
}
