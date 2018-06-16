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
import com.nicoqueijo.android.currencyconverter.algorithms.CurrencyConversion;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String BASE_URL = "http://apilayer.net/api/live";
    private static final String API_KEY_PARAM = "?access_key=";
    private static String API_KEY;
    private static final String FORMAT_PARAM = "&format=1";

    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mSharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreferences.edit();

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
                            updateSharedPreferencesExchangeRates(rates);
                            double fromRate = (double) mSharedPreferences.getFloat("USDARS", 0.0f);
                            double toRate = (double) mSharedPreferences.getFloat("USDUYU", 0.0f);
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

    private void updateSharedPreferencesExchangeRates(JSONObject rates) throws JSONException {
        mSharedPreferencesEditor.putFloat("USDAED", (float) rates.getDouble("USDAED"));
        mSharedPreferencesEditor.putFloat("USDAFN", (float) rates.getDouble("USDAFN"));
        mSharedPreferencesEditor.putFloat("USDALL", (float) rates.getDouble("USDALL"));
        mSharedPreferencesEditor.putFloat("USDAMD", (float) rates.getDouble("USDAMD"));
        mSharedPreferencesEditor.putFloat("USDANG", (float) rates.getDouble("USDANG"));
        mSharedPreferencesEditor.putFloat("USDAOA", (float) rates.getDouble("USDAOA"));
        mSharedPreferencesEditor.putFloat("USDARS", (float) rates.getDouble("USDARS"));
        mSharedPreferencesEditor.putFloat("USDAUD", (float) rates.getDouble("USDAUD"));
        mSharedPreferencesEditor.putFloat("USDAWG", (float) rates.getDouble("USDAWG"));
        mSharedPreferencesEditor.putFloat("USDAZN", (float) rates.getDouble("USDAZN"));
        mSharedPreferencesEditor.putFloat("USDBAM", (float) rates.getDouble("USDBAM"));
        mSharedPreferencesEditor.putFloat("USDBBD", (float) rates.getDouble("USDBBD"));
        mSharedPreferencesEditor.putFloat("USDBDT", (float) rates.getDouble("USDBDT"));
        mSharedPreferencesEditor.putFloat("USDBGN", (float) rates.getDouble("USDBGN"));
        mSharedPreferencesEditor.putFloat("USDBHD", (float) rates.getDouble("USDBHD"));
        mSharedPreferencesEditor.putFloat("USDBIF", (float) rates.getDouble("USDBIF"));
        mSharedPreferencesEditor.putFloat("USDBMD", (float) rates.getDouble("USDBMD"));
        mSharedPreferencesEditor.putFloat("USDBND", (float) rates.getDouble("USDBND"));
        mSharedPreferencesEditor.putFloat("USDBOB", (float) rates.getDouble("USDBOB"));
        mSharedPreferencesEditor.putFloat("USDBRL", (float) rates.getDouble("USDBRL"));
        mSharedPreferencesEditor.putFloat("USDBSD", (float) rates.getDouble("USDBSD"));
        mSharedPreferencesEditor.putFloat("USDBTC", (float) rates.getDouble("USDBTC"));
        mSharedPreferencesEditor.putFloat("USDBTN", (float) rates.getDouble("USDBTN"));
        mSharedPreferencesEditor.putFloat("USDBWP", (float) rates.getDouble("USDBWP"));
        mSharedPreferencesEditor.putFloat("USDBYN", (float) rates.getDouble("USDBYN"));
        mSharedPreferencesEditor.putFloat("USDBYR", (float) rates.getDouble("USDBYR"));
        mSharedPreferencesEditor.putFloat("USDBZD", (float) rates.getDouble("USDBZD"));
        mSharedPreferencesEditor.putFloat("USDCAD", (float) rates.getDouble("USDCAD"));
        mSharedPreferencesEditor.putFloat("USDCDF", (float) rates.getDouble("USDCDF"));
        mSharedPreferencesEditor.putFloat("USDCHF", (float) rates.getDouble("USDCHF"));
        mSharedPreferencesEditor.putFloat("USDCLF", (float) rates.getDouble("USDCLF"));
        mSharedPreferencesEditor.putFloat("USDCLP", (float) rates.getDouble("USDCLP"));
        mSharedPreferencesEditor.putFloat("USDCNY", (float) rates.getDouble("USDCNY"));
        mSharedPreferencesEditor.putFloat("USDCOP", (float) rates.getDouble("USDCOP"));
        mSharedPreferencesEditor.putFloat("USDCRC", (float) rates.getDouble("USDCRC"));
        mSharedPreferencesEditor.putFloat("USDCUC", (float) rates.getDouble("USDCUC"));
        mSharedPreferencesEditor.putFloat("USDCUP", (float) rates.getDouble("USDCUP"));
        mSharedPreferencesEditor.putFloat("USDCVE", (float) rates.getDouble("USDCVE"));
        mSharedPreferencesEditor.putFloat("USDCZK", (float) rates.getDouble("USDCZK"));
        mSharedPreferencesEditor.putFloat("USDDJF", (float) rates.getDouble("USDDJF"));
        mSharedPreferencesEditor.putFloat("USDDKK", (float) rates.getDouble("USDDKK"));
        mSharedPreferencesEditor.putFloat("USDDOP", (float) rates.getDouble("USDDOP"));
        mSharedPreferencesEditor.putFloat("USDDZD", (float) rates.getDouble("USDDZD"));
        mSharedPreferencesEditor.putFloat("USDEGP", (float) rates.getDouble("USDEGP"));
        mSharedPreferencesEditor.putFloat("USDERN", (float) rates.getDouble("USDERN"));
        mSharedPreferencesEditor.putFloat("USDETB", (float) rates.getDouble("USDETB"));
        mSharedPreferencesEditor.putFloat("USDEUR", (float) rates.getDouble("USDEUR"));
        mSharedPreferencesEditor.putFloat("USDFJD", (float) rates.getDouble("USDFJD"));
        mSharedPreferencesEditor.putFloat("USDFKP", (float) rates.getDouble("USDFKP"));
        mSharedPreferencesEditor.putFloat("USDGBP", (float) rates.getDouble("USDGBP"));
        mSharedPreferencesEditor.putFloat("USDGEL", (float) rates.getDouble("USDGEL"));
        mSharedPreferencesEditor.putFloat("USDGGP", (float) rates.getDouble("USDGGP"));
        mSharedPreferencesEditor.putFloat("USDGHS", (float) rates.getDouble("USDGHS"));
        mSharedPreferencesEditor.putFloat("USDGIP", (float) rates.getDouble("USDGIP"));
        mSharedPreferencesEditor.putFloat("USDGMD", (float) rates.getDouble("USDGMD"));
        mSharedPreferencesEditor.putFloat("USDGNF", (float) rates.getDouble("USDGNF"));
        mSharedPreferencesEditor.putFloat("USDGTQ", (float) rates.getDouble("USDGTQ"));
        mSharedPreferencesEditor.putFloat("USDGYD", (float) rates.getDouble("USDGYD"));
        mSharedPreferencesEditor.putFloat("USDHKD", (float) rates.getDouble("USDHKD"));
        mSharedPreferencesEditor.putFloat("USDHNL", (float) rates.getDouble("USDHNL"));
        mSharedPreferencesEditor.putFloat("USDHRK", (float) rates.getDouble("USDHRK"));
        mSharedPreferencesEditor.putFloat("USDHTG", (float) rates.getDouble("USDHTG"));
        mSharedPreferencesEditor.putFloat("USDHUF", (float) rates.getDouble("USDHUF"));
        mSharedPreferencesEditor.putFloat("USDIDR", (float) rates.getDouble("USDIDR"));
        mSharedPreferencesEditor.putFloat("USDILS", (float) rates.getDouble("USDILS"));
        mSharedPreferencesEditor.putFloat("USDIMP", (float) rates.getDouble("USDIMP"));
        mSharedPreferencesEditor.putFloat("USDINR", (float) rates.getDouble("USDINR"));
        mSharedPreferencesEditor.putFloat("USDIQD", (float) rates.getDouble("USDIQD"));
        mSharedPreferencesEditor.putFloat("USDIRR", (float) rates.getDouble("USDIRR"));
        mSharedPreferencesEditor.putFloat("USDISK", (float) rates.getDouble("USDISK"));
        mSharedPreferencesEditor.putFloat("USDJEP", (float) rates.getDouble("USDJEP"));
        mSharedPreferencesEditor.putFloat("USDJMD", (float) rates.getDouble("USDJMD"));
        mSharedPreferencesEditor.putFloat("USDJOD", (float) rates.getDouble("USDJOD"));
        mSharedPreferencesEditor.putFloat("USDJPY", (float) rates.getDouble("USDJPY"));
        mSharedPreferencesEditor.putFloat("USDKES", (float) rates.getDouble("USDKES"));
        mSharedPreferencesEditor.putFloat("USDKGS", (float) rates.getDouble("USDKGS"));
        mSharedPreferencesEditor.putFloat("USDKHR", (float) rates.getDouble("USDKHR"));
        mSharedPreferencesEditor.putFloat("USDKMF", (float) rates.getDouble("USDKMF"));
        mSharedPreferencesEditor.putFloat("USDKPW", (float) rates.getDouble("USDKPW"));
        mSharedPreferencesEditor.putFloat("USDKRW", (float) rates.getDouble("USDKRW"));
        mSharedPreferencesEditor.putFloat("USDKWD", (float) rates.getDouble("USDKWD"));
        mSharedPreferencesEditor.putFloat("USDKYD", (float) rates.getDouble("USDKYD"));
        mSharedPreferencesEditor.putFloat("USDKZT", (float) rates.getDouble("USDKZT"));
        mSharedPreferencesEditor.putFloat("USDLAK", (float) rates.getDouble("USDLAK"));
        mSharedPreferencesEditor.putFloat("USDLBP", (float) rates.getDouble("USDLBP"));
        mSharedPreferencesEditor.putFloat("USDLKR", (float) rates.getDouble("USDLKR"));
        mSharedPreferencesEditor.putFloat("USDLRD", (float) rates.getDouble("USDLRD"));
        mSharedPreferencesEditor.putFloat("USDLSL", (float) rates.getDouble("USDLSL"));
        mSharedPreferencesEditor.putFloat("USDLTL", (float) rates.getDouble("USDLTL"));
        mSharedPreferencesEditor.putFloat("USDLVL", (float) rates.getDouble("USDLVL"));
        mSharedPreferencesEditor.putFloat("USDLYD", (float) rates.getDouble("USDLYD"));
        mSharedPreferencesEditor.putFloat("USDMAD", (float) rates.getDouble("USDMAD"));
        mSharedPreferencesEditor.putFloat("USDMDL", (float) rates.getDouble("USDMDL"));
        mSharedPreferencesEditor.putFloat("USDMGA", (float) rates.getDouble("USDMGA"));
        mSharedPreferencesEditor.putFloat("USDMKD", (float) rates.getDouble("USDMKD"));
        mSharedPreferencesEditor.putFloat("USDMMK", (float) rates.getDouble("USDMMK"));
        mSharedPreferencesEditor.putFloat("USDMNT", (float) rates.getDouble("USDMNT"));
        mSharedPreferencesEditor.putFloat("USDMOP", (float) rates.getDouble("USDMOP"));
        mSharedPreferencesEditor.putFloat("USDMRO", (float) rates.getDouble("USDMRO"));
        mSharedPreferencesEditor.putFloat("USDMUR", (float) rates.getDouble("USDMUR"));
        mSharedPreferencesEditor.putFloat("USDMVR", (float) rates.getDouble("USDMVR"));
        mSharedPreferencesEditor.putFloat("USDMWK", (float) rates.getDouble("USDMWK"));
        mSharedPreferencesEditor.putFloat("USDMXN", (float) rates.getDouble("USDMXN"));
        mSharedPreferencesEditor.putFloat("USDMYR", (float) rates.getDouble("USDMYR"));
        mSharedPreferencesEditor.putFloat("USDMZN", (float) rates.getDouble("USDMZN"));
        mSharedPreferencesEditor.putFloat("USDNAD", (float) rates.getDouble("USDNAD"));
        mSharedPreferencesEditor.putFloat("USDNGN", (float) rates.getDouble("USDNGN"));
        mSharedPreferencesEditor.putFloat("USDNIO", (float) rates.getDouble("USDNIO"));
        mSharedPreferencesEditor.putFloat("USDNOK", (float) rates.getDouble("USDNOK"));
        mSharedPreferencesEditor.putFloat("USDNPR", (float) rates.getDouble("USDNPR"));
        mSharedPreferencesEditor.putFloat("USDNZD", (float) rates.getDouble("USDNZD"));
        mSharedPreferencesEditor.putFloat("USDOMR", (float) rates.getDouble("USDOMR"));
        mSharedPreferencesEditor.putFloat("USDPAB", (float) rates.getDouble("USDPAB"));
        mSharedPreferencesEditor.putFloat("USDPEN", (float) rates.getDouble("USDPEN"));
        mSharedPreferencesEditor.putFloat("USDPGK", (float) rates.getDouble("USDPGK"));
        mSharedPreferencesEditor.putFloat("USDPHP", (float) rates.getDouble("USDPHP"));
        mSharedPreferencesEditor.putFloat("USDPKR", (float) rates.getDouble("USDPKR"));
        mSharedPreferencesEditor.putFloat("USDPLN", (float) rates.getDouble("USDPLN"));
        mSharedPreferencesEditor.putFloat("USDPYG", (float) rates.getDouble("USDPYG"));
        mSharedPreferencesEditor.putFloat("USDQAR", (float) rates.getDouble("USDQAR"));
        mSharedPreferencesEditor.putFloat("USDRON", (float) rates.getDouble("USDRON"));
        mSharedPreferencesEditor.putFloat("USDRSD", (float) rates.getDouble("USDRSD"));
        mSharedPreferencesEditor.putFloat("USDRUB", (float) rates.getDouble("USDRUB"));
        mSharedPreferencesEditor.putFloat("USDRWF", (float) rates.getDouble("USDRWF"));
        mSharedPreferencesEditor.putFloat("USDSAR", (float) rates.getDouble("USDSAR"));
        mSharedPreferencesEditor.putFloat("USDSBD", (float) rates.getDouble("USDSBD"));
        mSharedPreferencesEditor.putFloat("USDSCR", (float) rates.getDouble("USDSCR"));
        mSharedPreferencesEditor.putFloat("USDSDG", (float) rates.getDouble("USDSDG"));
        mSharedPreferencesEditor.putFloat("USDSEK", (float) rates.getDouble("USDSEK"));
        mSharedPreferencesEditor.putFloat("USDSGD", (float) rates.getDouble("USDSGD"));
        mSharedPreferencesEditor.putFloat("USDSHP", (float) rates.getDouble("USDSHP"));
        mSharedPreferencesEditor.putFloat("USDSLL", (float) rates.getDouble("USDSLL"));
        mSharedPreferencesEditor.putFloat("USDSOS", (float) rates.getDouble("USDSOS"));
        mSharedPreferencesEditor.putFloat("USDSRD", (float) rates.getDouble("USDSRD"));
        mSharedPreferencesEditor.putFloat("USDSTD", (float) rates.getDouble("USDSTD"));
        mSharedPreferencesEditor.putFloat("USDSVC", (float) rates.getDouble("USDSVC"));
        mSharedPreferencesEditor.putFloat("USDSYP", (float) rates.getDouble("USDSYP"));
        mSharedPreferencesEditor.putFloat("USDSZL", (float) rates.getDouble("USDSZL"));
        mSharedPreferencesEditor.putFloat("USDTHB", (float) rates.getDouble("USDTHB"));
        mSharedPreferencesEditor.putFloat("USDTJS", (float) rates.getDouble("USDTJS"));
        mSharedPreferencesEditor.putFloat("USDTMT", (float) rates.getDouble("USDTMT"));
        mSharedPreferencesEditor.putFloat("USDTND", (float) rates.getDouble("USDTND"));
        mSharedPreferencesEditor.putFloat("USDTOP", (float) rates.getDouble("USDTOP"));
        mSharedPreferencesEditor.putFloat("USDTRY", (float) rates.getDouble("USDTRY"));
        mSharedPreferencesEditor.putFloat("USDTTD", (float) rates.getDouble("USDTTD"));
        mSharedPreferencesEditor.putFloat("USDTWD", (float) rates.getDouble("USDTWD"));
        mSharedPreferencesEditor.putFloat("USDTZS", (float) rates.getDouble("USDTZS"));
        mSharedPreferencesEditor.putFloat("USDUAH", (float) rates.getDouble("USDUAH"));
        mSharedPreferencesEditor.putFloat("USDUGX", (float) rates.getDouble("USDUGX"));
        mSharedPreferencesEditor.putFloat("USDUSD", (float) rates.getDouble("USDUSD"));
        mSharedPreferencesEditor.putFloat("USDUYU", (float) rates.getDouble("USDUYU"));
        mSharedPreferencesEditor.putFloat("USDUZS", (float) rates.getDouble("USDUZS"));
        mSharedPreferencesEditor.putFloat("USDVEF", (float) rates.getDouble("USDVEF"));
        mSharedPreferencesEditor.putFloat("USDVND", (float) rates.getDouble("USDVND"));
        mSharedPreferencesEditor.putFloat("USDVUV", (float) rates.getDouble("USDVUV"));
        mSharedPreferencesEditor.putFloat("USDWST", (float) rates.getDouble("USDWST"));
        mSharedPreferencesEditor.putFloat("USDXAF", (float) rates.getDouble("USDXAF"));
        mSharedPreferencesEditor.putFloat("USDXAG", (float) rates.getDouble("USDXAG"));
        mSharedPreferencesEditor.putFloat("USDXAU", (float) rates.getDouble("USDXAU"));
        mSharedPreferencesEditor.putFloat("USDXCD", (float) rates.getDouble("USDXCD"));
        mSharedPreferencesEditor.putFloat("USDXDR", (float) rates.getDouble("USDXDR"));
        mSharedPreferencesEditor.putFloat("USDXOF", (float) rates.getDouble("USDXOF"));
        mSharedPreferencesEditor.putFloat("USDXPF", (float) rates.getDouble("USDXPF"));
        mSharedPreferencesEditor.putFloat("USDYER", (float) rates.getDouble("USDYER"));
        mSharedPreferencesEditor.putFloat("USDZAR", (float) rates.getDouble("USDZAR"));
        mSharedPreferencesEditor.putFloat("USDZMK", (float) rates.getDouble("USDZMK"));
        mSharedPreferencesEditor.putFloat("USDZMW", (float) rates.getDouble("USDZMW"));
        mSharedPreferencesEditor.putFloat("USDZWL", (float) rates.getDouble("USDZWL"));
    }

    /**
     * Initializes the API key from a local private file
     * that is not tracked by Git for obvious reasons.
     */
    private void initApiKey() {
        API_KEY = getResources().getString(R.string.api_key);
    }
}
