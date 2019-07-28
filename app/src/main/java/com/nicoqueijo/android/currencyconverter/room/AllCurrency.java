package com.nicoqueijo.android.currencyconverter.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "all_currency")
public class AllCurrency {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "currency_code")
    private String currencyCode;
    @ColumnInfo(name = "currency_value")
    private double currencyValue;

    public AllCurrency(String currencyCode, double currencyValue) {
        this.currencyCode = currencyCode;
        this.currencyValue = currencyValue;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public double getCurrencyValue() {
        return currencyValue;
    }

    public void setCurrencyValue(double currencyValue) {
        this.currencyValue = currencyValue;
    }
}
