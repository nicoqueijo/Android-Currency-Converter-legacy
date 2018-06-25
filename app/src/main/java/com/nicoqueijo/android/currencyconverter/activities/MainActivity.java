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
                            // Encapsulate this code into method(s)
                            // Get the value in "success key", if it is false handle it someway,
                            // else process the JSON to update rates.
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {
                                long timestamp = jsonObject.getLong("timestamp");
                                JSONObject rates = jsonObject.getJSONObject("quotes");
                                updateSharedPreferencesExchangeRates(rates);
                            } else {
                                // Handle what happens when user HAS access to internet but
                                // api is not working for some reason.

                                // If there are already values in sharedpreferences then use
                                // those and go on as usual showing (possibly) outdated rates.
                                Log.d(TAG, "onResponse: " + jsonObject.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getLocalizedMessage());
                // Handle what happens when user doesn't have access to internet
            }
        });

        // Add the request to the RequestQueue.
        volleyRequestQueue.add(stringRequest);
    }

    /**
     * Traverses the JSON object of the exchange rates
     * and saves them locally via SharedPreferences.
     *
     * @param rates the JSON object containing the exchange rates.
     * @throws JSONException in case a key being fetched doesn't exist.
     */
    private void updateSharedPreferencesExchangeRates(JSONObject rates) throws JSONException {
        SharedPreferences.Editor mSharedPreferencesEditor = mSharedPreferences.edit();
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
