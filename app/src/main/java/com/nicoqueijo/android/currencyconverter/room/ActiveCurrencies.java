package com.nicoqueijo.android.currencyconverter.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ActiveCurrencies {

    @PrimaryKey
    public int currencyOrder;
    public String currencyCode;
}
