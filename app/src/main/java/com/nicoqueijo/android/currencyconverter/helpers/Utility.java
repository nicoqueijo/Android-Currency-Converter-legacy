package com.nicoqueijo.android.currencyconverter.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.databinding.BindingAdapter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicoqueijo.android.currencyconverter.R;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Provides useful, general-purpose methods to be used across the project.
 */
public final class Utility {

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
     * Used to manipulate how a resource value is set to an image view using Data Binding.
     *
     * @param imageView the imageView to be displayed
     * @param resource  the resource id of the drawable
     */
    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }

    /**
     * After migrating to SQLite this method is obsolete but preserved just in case.
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
     * After migrating to SQLite this method is obsolete but preserved just in case.
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
        final int decimalPlaces = 2;
        return value.setScale(decimalPlaces, RoundingMode.CEILING);
    }

    /**
     * Allows background_dialog.xml to take effect and round the Dialog's corners.
     * Credit: https://stackoverflow.com/a/28937224/5906793
     */
    public static void roundDialogCorners(DialogFragment dialogFragment) {
        dialogFragment.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color
                .TRANSPARENT));
    }

    /**
     * Checks weather there is currently an active internet connection.
     *
     * @param activity hosting activity from where this method is being called from.
     * @return whether there is an internet connection.
     */
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Gets the resource id of the current theme.
     *
     * @param context application environment.
     * @return the resource id as an int.
     */
    private static int getThemeId(Context context) {
        try {
            Class<?> wrapper = Context.class;
            Method method = wrapper.getMethod("getThemeResId");
            method.setAccessible(true);
            return (Integer) method.invoke(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Changes the Snackbar's colors based on the currently set theme.
     *
     * @param snackbar the Snackbar to style.
     * @param context  environment where this Snackbar resides.
     */
    public static void styleSnackbar(Snackbar snackbar, Context context) {
        int themeId = getThemeId(context);
        View snackbarView = snackbar.getView();
        int snackbarTextId = com.google.android.material.R.id.snackbar_text;
//        int snackbarTextId = android.support.design.R.id.snackbar_text;
        TextView textView = snackbarView.findViewById(snackbarTextId);
        switch (themeId) {
            case R.style.AppThemeDark:
                textView.setTextColor(Color.BLACK);
                snackbarView.setBackgroundColor(Color.WHITE);
                snackbar.setActionTextColor(snackbar.getContext().getResources()
                        .getColor(R.color.purple_800));
                break;
            case R.style.AppThemeLight:
                snackbar.setActionTextColor(snackbar.getContext().getResources()
                        .getColor(R.color.purple_200));
                break;
        }
    }

    /*
    Leaving these here for quick copy+paste when I need to measure performance.

        long startTime = System.nanoTime();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
    */
}
