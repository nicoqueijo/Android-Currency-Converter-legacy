package com.nicoqueijo.android.currencyconverter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nicoqueijo.android.currencyconverter.Conversion.CurrencyConversion;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String BASE_URL = "http://apilayer.net/api/live";
    private static final String API_KEY_PARAM = "?access_key=";
    private static String API_KEY;
    private static final String FORMAT_PARAM = "&format=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initApiKey();
        String fullUrl = BASE_URL + API_KEY_PARAM + API_KEY + FORMAT_PARAM;

        // Instantiate the RequestQueue.
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, fullUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int amount = 2425;

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject rates = jsonObject.getJSONObject("quotes");
                            double fromRate = rates.getDouble("USDARS");
                            double toRate = rates.getDouble("USDUYU");
                            String result = Double.toString(CurrencyConversion
                                    .currencyConverter(amount, fromRate, toRate));
                            Log.d(TAG, "onResponse: " + result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getLocalizedMessage());
            }
        });

        // Add the request to the RequestQueue.
        volleyRequestQueue.add(stringRequest);
    }

    /**
     * Initializes the API key from a local private file
     * that is not tracked by Git for obvious reasons.
     */
    private void initApiKey() {
        API_KEY = getResources().getString(R.string.api_key);
    }
}
