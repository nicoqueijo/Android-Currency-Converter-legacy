package com.nicoqueijo.android.currencyconverter.interfaces;

import com.nicoqueijo.android.currencyconverter.models.Currency;

/**
 * Used to communicate between Fragments. When a currency is selected in the
 * SelectCurrenciesFragment this interface is used to notify the ActiveCurrenciesFragment
 * so it can be added to its list. This is done via the MainActivity.
 */
public interface ICommunicator {
    void passSelectedCurrency(Currency currency);
}
