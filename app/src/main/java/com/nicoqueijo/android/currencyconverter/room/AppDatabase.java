package com.nicoqueijo.android.currencyconverter.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {AllCurrency.class, ActiveCurrency.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
}
