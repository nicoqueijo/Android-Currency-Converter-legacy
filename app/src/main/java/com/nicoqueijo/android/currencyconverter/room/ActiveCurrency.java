package com.nicoqueijo.android.currencyconverter.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "active_currency", foreignKeys = @ForeignKey(entity = AllCurrency.class,
        parentColumns = "currency_code", childColumns = "currency_code"))
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
