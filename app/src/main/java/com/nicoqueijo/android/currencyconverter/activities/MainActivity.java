package com.nicoqueijo.android.currencyconverter.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nicoqueijo.android.currencyconverter.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String BASE_URL = "http://apilayer.net/api/live";
    private static final String API_KEY_PARAM = "?access_key=";
    private static String API_KEY;
    private static final String FORMAT_PARAM = "&format=1";

    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        initApiKey();
        String fullUrl = BASE_URL + API_KEY_PARAM + API_KEY + FORMAT_PARAM;

        // Instantiate the RequestQueue.
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, fullUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                updateSharedPreferencesExchangeRates(jsonObject);
                                // Go on with the app after updating rates
                            } else if (!isSharedPreferencesEmpty()) {
                                // Go on with the app using existing rates
                            } else {
                                JSONObject error = jsonObject.getJSONObject("error");
                                final int INDENT_SPACES = 4;

                                // To be displayed in a "Show more" View to supplement
                                // a generic error message.
                                String errorDetails = error.toString(INDENT_SPACES);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getLocalizedMessage());
                // First time launching?
                //      Display error about not being able to fetch exchange rates from cloud.
                // Else:
                //      Proceed with current values in SharedPreferences
            }
        });

        // Add the request to the RequestQueue.
        volleyRequestQueue.add(stringRequest);
    }

    /**
     * Determines if values have been previously stored in SharedPreferences
     * by attempting to fetch the value of the timestamp key.
     *
     * @return whether 0 was returned by default due to the timestamp key being null.
     */
    private boolean isSharedPreferencesEmpty() {
        long value = mSharedPreferences.getLong("timestamp", 0L);
        return value == 0L;
    }

    /**
     * Extracts the timestamp and exchange rates from the JSON
     * object and saves them locally via SharedPreferences.
     *
     * @param jsonObject the JSON object containing the exchange rates and timestamp.
     * @throws JSONException in case a key being fetched doesn't exist.
     */
    private void updateSharedPreferencesExchangeRates(JSONObject jsonObject) throws JSONException {
        SharedPreferences.Editor mSharedPreferencesEditor = mSharedPreferences.edit();
        long timestamp = jsonObject.getLong("timestamp");
        mSharedPreferencesEditor.putLong("timestamp", timestamp);
        JSONObject rates = jsonObject.getJSONObject("quotes");
        JSONArray keys = rates.names();
        for (int i = 0; i < keys.length(); i++) {
            String key = keys.getString(i);
            double value = rates.getDouble(key);
            putDouble(mSharedPreferencesEditor, key, value);
        }
        mSharedPreferencesEditor.apply();
    }

    /**
     * Used to store doubles in SharedPreferences without losing precision.
     * Source: https://stackoverflow.com/a/18098090/5906793
     */
    private void putDouble(final SharedPreferences.Editor edit,
                           final String key, final double value) {
        edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    /**
     * Used to retrieve doubles in SharedPreferences without losing precision.
     * Source: https://stackoverflow.com/a/18098090/5906793
     */
    private double getDouble(final SharedPreferences prefs,
                             final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    /**
     * Initializes the API key from a local private file
     * that is not tracked by Git for obvious reasons.
     */
    private void initApiKey() {
        API_KEY = getResources().getString(R.string.api_key);
    }
}
