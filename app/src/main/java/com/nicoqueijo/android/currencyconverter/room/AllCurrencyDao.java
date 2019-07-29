package com.nicoqueijo.android.currencyconverter.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AllCurrencyDao {

    @Insert
    void insert(AllCurrency currency);

    @Update
    void update(AllCurrency currency);

    @Delete
    void delete(AllCurrency currency);

    @Query("SELECT * FROM all_currency ORDER BY currency_code ASC")
    List<AllCurrency> getAllCurrencies();

    @Query("DELETE FROM all_currency")
    void deleteAll();
}
