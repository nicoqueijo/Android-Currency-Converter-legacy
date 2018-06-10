package com.nicoqueijo.android.currencyconverter.Conversion;

public class CurrencyConversion {

    private static final String TAG = "CurrencyConversion";

    public static double currencyConverter(int amount, double fromRate, double toRate) {
        double valueInDollars = convertAnyCurrencyToDollar(amount, fromRate);
        double valueInTargetCurrency = convertDollarToAnyCurrency(valueInDollars, toRate);
        return valueInTargetCurrency;
    }

    private static double convertAnyCurrencyToDollar(int amount, double fromRate) {
        return (amount / fromRate);
    }

    private static double convertDollarToAnyCurrency(double dollarValue, double toRate) {
        return (dollarValue * toRate);
    }
}
