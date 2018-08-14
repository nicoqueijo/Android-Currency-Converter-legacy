package com.nicoqueijo.android.currencyconverter.interfaces;

import com.nicoqueijo.android.currencyconverter.models.Currency;

/**
 * Used to communicate between fragments. When a currency is selected in the
 * SelectExchangeRatesDialog this interface is used to notify the
 * ActiveExchangeRatesFragment so it can be added to its list.
 */
public interface ICommunicator {
    void passSelectedCurrency(Currency currency);
}
