package com.nicoqueijo.android.currencyconverter.helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Provides useful, general-purpose methods to be used across the project.
 */
public class Utility {
    /**
     * Retrieves string resources using a String instead of an int.
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
     *
     * @param name    name of the drawable resource
     * @param context the context from which this method is being called
     * @return the drawable resource id
     */
    public static int getDrawableResourceByName(String name, Context context) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    /**
     * Used to store doubles in SharedPreferences without losing precision.
     * Source: https://stackoverflow.com/a/18098090/5906793
     */
    public static void putDouble(final SharedPreferences.Editor edit,
                                 final String key, final double value) {
        edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    /**
     * Used to retrieve doubles in SharedPreferences without losing precision.
     * Source: https://stackoverflow.com/a/18098090/5906793
     */
    public static double getDouble(final SharedPreferences prefs,
                                   final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }
}
