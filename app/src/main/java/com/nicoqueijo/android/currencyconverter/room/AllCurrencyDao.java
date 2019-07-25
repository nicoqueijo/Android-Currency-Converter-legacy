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

    @Query("SELECT active_currency.currency_order, active_currency.currency_code, " +
            "all_currency.currency_value FROM active_currency INNER JOIN all_currency " +
            "WHERE all_currency.currency_code =  active_currency.currency_code")
    List<ActiveCurrency> getActiveCurrencies();

    @Query("DELETE FROM all_currency")
    void deleteAll();
}
