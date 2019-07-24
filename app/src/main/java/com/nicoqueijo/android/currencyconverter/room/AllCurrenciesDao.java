package com.nicoqueijo.android.currencyconverter.room;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AllCurrenciesDao {

    @Query("SELECT * FROM AllCurrencies")
    List<ActiveCurrencies> getAllCurrencies();
}
