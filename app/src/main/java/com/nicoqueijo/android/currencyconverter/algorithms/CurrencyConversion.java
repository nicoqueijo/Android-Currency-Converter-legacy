package com.nicoqueijo.android.currencyconverter.algorithms;

/**
 * Class dedicated to perform currency conversions.
 * Exchange rates are quoted in values against the US dollar.
 */
public class CurrencyConversion {

    private static final String TAG = "CurrencyConversion";

    /**
     * Converts a given amount from one currency to another using two helper methods.
     *
     * @param amount   the amount to be converted.
     * @param fromRate the exchange rate of the currency being converted.
     * @param toRate   the exchange rate of the target currency.
     * @return the amount converted to the target currency.
     */
    public static double currencyConverter(int amount, double fromRate, double toRate) {
        double valueInDollars = convertAnyCurrencyToDollar(amount, fromRate);
        double valueInTargetCurrency = convertDollarToAnyCurrency(valueInDollars, toRate);
        return valueInTargetCurrency;
    }

    /**
     * Converts a given amount to USD.
     *
     * @param amount   the amount to be converted.
     * @param fromRate the exchange rate of the currency being converted.
     * @return the amount converted to USD.
     */
    private static double convertAnyCurrencyToDollar(int amount, double fromRate) {
        return (amount / fromRate);
    }

    /**
     * Converts a USD amount to another currency.
     *
     * @param dollarValue an amount in USD.
     * @param toRate      the exchange rate of the target currency.
     * @return the dollar amount converted to the target currency.
     */
    private static double convertDollarToAnyCurrency(double dollarValue, double toRate) {
        return (dollarValue * toRate);
    }
}
