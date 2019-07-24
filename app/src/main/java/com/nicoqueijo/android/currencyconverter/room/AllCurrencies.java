package com.nicoqueijo.android.currencyconverter.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AllCurrencies {

    @PrimaryKey
    public String currencyCode;
    public double currencyValue;
}
