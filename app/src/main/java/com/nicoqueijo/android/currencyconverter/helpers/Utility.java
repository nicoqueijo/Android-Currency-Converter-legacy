package com.nicoqueijo.android.currencyconverter.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Provides useful, general-purpose methods to be used across the project.
 */
public class Utility {
    /**
     * Retrieves string resources using a String instead of an int.
     * Credit: https://stackoverflow.com/a/11595723/5906793
     *
     * @param name    name of the string resource
     * @param context the context from which this method is being called
     * @return the string resource
     */
    public static String getStringResourceByName(String name, Context context) {
        int resId = context.getResources().getIdentifier(name, "string", context.getPackageName());
        return context.getString(resId);
    }

    /**
     * Retrieves drawable resources using a String instead of an int.
     * Credit: https://stackoverflow.com/a/11595723/5906793
     *
     * @param name    name of the drawable resource
     * @param context the context from which this method is being called
     * @return the drawable resource id
     */
    public static int getDrawableResourceByName(String name, Context context) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    /**
     * Stores doubles in SharedPreferences without losing precision.
     * Credit: https://stackoverflow.com/a/18098090/5906793
     *
     * @param edit  sharedPrefs editor used to store the value
     * @param key   identifier to map the value
     * @param value self-explanatory
     */
    public static void putDouble(final SharedPreferences.Editor edit, final String key,
                                 final double value) {
        edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    /**
     * Retrieves doubles in SharedPreferences without losing precision.
     * Credit: https://stackoverflow.com/a/18098090/5906793
     *
     * @param prefs        sharedPrefs used to retrieve the value
     * @param key          the identifier to access the value
     * @param defaultValue value to set if key is not found
     * @return the value as a double
     */
    public static double getDouble(final SharedPreferences prefs, final String key,
                                   final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    /**
     * Rounds a BigDecimal value to two decimal places.
     * Credit: https://stackoverflow.com/a/15643364/5906793
     *
     * @param value the value to be rounded
     * @return the rounded value
     */
    public static BigDecimal roundBigDecimal(BigDecimal value) {
        final int DECIMAL_PLACES = 2;
        return value.setScale(DECIMAL_PLACES, RoundingMode.CEILING);
    }
}
