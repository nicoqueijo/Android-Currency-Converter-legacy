package com.nicoqueijo.android.currencyconverter.algorithms;

import java.math.BigDecimal;

/**
 * Class dedicated to perform currency conversions.
 * Exchange rates are quoted in values against the US dollar.
 */
public class CurrencyConversion {

    public static final String TAG = CurrencyConversion.class.getSimpleName();

    /**
     * Converts a given amount from one currency to another using two helper methods.
     *
     * @param amount   the amount to be converted.
     * @param fromRate the exchange rate of the currency being converted.
     * @param toRate   the exchange rate of the target currency.
     * @return the amount converted to the target currency.
     */
    public static BigDecimal currencyConverter(BigDecimal amount, double fromRate, double toRate) {
        BigDecimal valueInDollars = convertAnyCurrencyToDollar(amount, fromRate);
        return convertDollarToAnyCurrency(valueInDollars, toRate);
    }

    /**
     * Converts a given amount to USD.
     *
     * @param amount   the amount to be converted.
     * @param fromRate the exchange rate of the currency being converted.
     * @return the amount converted to USD.
     */
    private static BigDecimal convertAnyCurrencyToDollar(BigDecimal amount, double fromRate) {
        return (amount.divide(BigDecimal.valueOf(fromRate)));
    }

    /**
     * Converts a USD amount to another currency.
     *
     * @param dollarValue an amount in USD.
     * @param toRate      the exchange rate of the target currency.
     * @return the dollar amount converted to the target currency.
     */
    private static BigDecimal convertDollarToAnyCurrency(BigDecimal dollarValue, double toRate) {
        return (dollarValue.multiply(BigDecimal.valueOf(toRate)));
    }
}
