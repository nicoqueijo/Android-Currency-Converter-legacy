package com.nicoqueijo.android.currencyconverter.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ActiveCurrencyDao {

    @Insert
    void insert(ActiveCurrency currency);

    @Update
    void update(ActiveCurrency currency);

    @Delete
    void delete(ActiveCurrency currency);

    @Query("SELECT * FROM active_currency")
    List<ActiveCurrency> getActiveCurrencies();

    @Query("DELETE FROM active_currency")
    void deleteAll();
}
