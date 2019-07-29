package com.nicoqueijo.android.currencyconverter.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "active_currency")
public class ActiveCurrency {

    @PrimaryKey
    @ColumnInfo(name = "currency_order")
    private int currencyOrder;
    @ColumnInfo(name = "currency_code")
    private String currencyCode;

    public ActiveCurrency(int currencyOrder, String currencyCode) {
        this.currencyOrder = currencyOrder;
        this.currencyCode = currencyCode;
    }

    public int getCurrencyOrder() {
        return currencyOrder;
    }

    public void setCurrencyOrder(int currencyOrder) {
        this.currencyOrder = currencyOrder;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
