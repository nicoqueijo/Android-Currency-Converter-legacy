package com.nicoqueijo.android.currencyconverter.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Currency implements Parcelable {

    public static final String TAG = Currency.class.getSimpleName();

    private String currencyCode;
    private double exchangeRate;
    private boolean selected;

    public Currency(String currencyCode, double exchangeRate) {
        this.currencyCode = currencyCode;
        this.exchangeRate = exchangeRate;
        this.selected = false;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Currency(Parcel in) {
        currencyCode = in.readString();
        exchangeRate = in.readDouble();
        selected = in.readByte() != 0;
    }

    public static final Creator<Currency> CREATOR = new Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currencyCode);
        dest.writeDouble(exchangeRate);
        dest.writeByte((byte) (selected ? 1 : 0));
    }
}
