package com.nicoqueijo.android.currencyconverter.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.nicoqueijo.android.currencyconverter.helpers.Constants;

import java.math.BigDecimal;

/**
 * Model class for a currency.
 * Implements Parcelable so objects of this type can be passed between activities/fragments and or
 * be saved in Bundles.
 */
public class Currency implements Parcelable {

    public static final String TAG = Currency.class.getSimpleName();

    private String currencyCode;
    private double exchangeRate;
    private BigDecimal conversionValue;
    private boolean selected;

    /**
     * Constructor for creating Currency objects.
     *
     * @param currencyCode currency code as it comes from API
     * @param exchangeRate exchange rate as it comes from API
     */
    public Currency(String currencyCode, double exchangeRate) {
        this.currencyCode = currencyCode;
        this.exchangeRate = exchangeRate;
        this.conversionValue = new BigDecimal(0.0);
        this.selected = false;
    }

    // Getters and setters are defined below.
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

    public BigDecimal getConversionValue() {
        return conversionValue;
    }

    public void setConversionValue(BigDecimal conversionValue) {
        this.conversionValue = conversionValue;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object obj) {
        Currency currency = (Currency) obj;
        return currency.getCurrencyCode().equals(this.currencyCode);
    }

    /**
     * Fetches the actual currency code.
     * The currency codes in shared_prefs and strings xml files are stored with "USD" preceded
     * to them as that's how they come from the API. For example the British Pound currency code
     * (GDP) is stored as "USDGDP". To access the actual currency code we must substring "USDGDP"
     * from index 3 onwards to retrieve "GDP".
     *
     * @return the trimmed currency code
     */
    public String getTrimmedCurrencyCode() {
        return currencyCode.substring(Constants.CURRENCY_CODE_STARTING_INDEX);
    }

    // Required, auto-generated code in order to implement Parcelable.
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currencyCode);
        dest.writeDouble(exchangeRate);
        dest.writeByte((byte) (selected ? 1 : 0));
    }
}
