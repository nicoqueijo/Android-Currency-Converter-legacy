package com.nicoqueijo.android.currencyconverter.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "all_currency")
// indices = {@Index(value = {"currency_code"}, unique = true)}
public class AllCurrency {

    @PrimaryKey
    @ColumnInfo(name = "currency_code")
//    index = true
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
