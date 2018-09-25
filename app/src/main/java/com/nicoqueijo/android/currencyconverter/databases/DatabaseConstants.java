package com.nicoqueijo.android.currencyconverter.databases;

public class DatabaseConstants {

    public static final String TAG = DatabaseConstants.class.getSimpleName();

    public static final String DATABASE_NAME = "currencies.db";
    public static final int VERSION_NUMBER = 1;

    public class TableAllCurrencies {

        public static final String TABLE_NAME = "all_currencies";
        public static final String COLUMN_CURRENCY_CODE = "currency_code";
        public static final String COLUMN_CURRENCY_VALUE = "currency_value";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
                + " ("
                + COLUMN_CURRENCY_CODE + " TEXT PRIMARY KEY, "
                + COLUMN_CURRENCY_VALUE + " INTEGER"
                + ");";
    }

    public class TableActiveCurrencies {

        public static final String TABLE_NAME = "active_currencies";
        public static final String COLUMN_ORDER = "currency_order";
        public static final String COLUMN_CURRENCY_CODE = "currency_code";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
                + " ("
                + COLUMN_ORDER + " INTEGER PRIMARY KEY, "
                + COLUMN_CURRENCY_CODE + " TEXT, "
                + "FOREIGN KEY (" + COLUMN_CURRENCY_CODE + ") REFERENCES "
                + TableAllCurrencies.TABLE_NAME
                + "(" + TableAllCurrencies.COLUMN_CURRENCY_CODE + ")" + ");";
    }
}
