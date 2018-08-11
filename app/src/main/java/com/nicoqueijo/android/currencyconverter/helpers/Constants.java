package com.nicoqueijo.android.currencyconverter.helpers;

public final class Constants {
    /**
     * The currency codes in the shared_prefs and strings xml files are stored with "USD" preceded
     * to them as that's how they come from the API. For example the British Pound currency code
     * (GDP) is stored as "USDGDP". To access the actual currency code we must substring "USDGDP"
     * from index 3 onwards to retrieve "GDP".
     */
    public static final int CURRENCY_CODE_STARTING_INDEX = 3;

    public static final int INDENT_SPACES = 4;

    public static final String ARG_ALL_CURRENCIES = "arg_all_currencies";

    public static final String ARG_ACTIVE_CURRENCIES = "arg_active_currencies";
}
