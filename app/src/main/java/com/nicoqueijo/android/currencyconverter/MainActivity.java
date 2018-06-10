package com.nicoqueijo.android.currencyconverter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.nicoqueijo.android.currencyconverter.Conversion.CurrencyConversion;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Test values. Later these will come from a web API.
    private double USD = 1.00;
    private double GBP = 0.746550;
    private double INR = 67.540594;
    private double AUD = 1.315496;
    private double CAD = 1.292939;
    private double SGD = 1.335317;
    private double CHF = 0.985305;
    private double MYR = 3.990059;
    private double JPY = 109.549355;
    private double CNY = 6.402751;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Testing the algorithm
        Log.d(TAG, "488 USD in MYR: " + CurrencyConversion.currencyConverter(488, USD, MYR));
        Log.d(TAG, "577 SGD in CHF: " + CurrencyConversion.currencyConverter(577, SGD, CHF));
        Log.d(TAG, "622 JPY in AUD: " + CurrencyConversion.currencyConverter(622, JPY, AUD));
        Log.d(TAG, "235 AUD in SGD: " + CurrencyConversion.currencyConverter(235, AUD, SGD));
        Log.d(TAG, "822 CAD in CHF: " + CurrencyConversion.currencyConverter(822, CAD, CHF));
        Log.d(TAG, "318 GBP in INR: " + CurrencyConversion.currencyConverter(318, GBP, INR));
        Log.d(TAG, "452 CHF in CNY: " + CurrencyConversion.currencyConverter(452, CHF, CNY));
        Log.d(TAG, "528 USD in SGD: " + CurrencyConversion.currencyConverter(528, USD, SGD));
        Log.d(TAG, "117 CHF in AUD: " + CurrencyConversion.currencyConverter(117, CHF, AUD));
        Log.d(TAG, "961 INR in MYR: " + CurrencyConversion.currencyConverter(961, INR, MYR));
    }
}
