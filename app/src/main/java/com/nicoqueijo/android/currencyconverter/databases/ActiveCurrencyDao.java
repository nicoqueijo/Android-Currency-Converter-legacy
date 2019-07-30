package com.nicoqueijo.android.currencyconverter.databases;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data access object for the "active_currencies" table.
 */
@Dao
public interface ActiveCurrencyDao {

    @Insert
    void insert(ActiveCurrency currency);

    @Update
    void update(ActiveCurrency currency);

    @Delete
    void delete(ActiveCurrency currency);

    @Query("SELECT * FROM active_currency ORDER BY currency_order ASC")
    List<ActiveCurrency> getActiveCurrencies();

    @Query("DELETE FROM active_currency")
    void deleteAll();
}
