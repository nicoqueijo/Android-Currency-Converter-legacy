package com.nicoqueijo.android.currencyconverter.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.fragments.NoInternetFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String BASE_URL = "http://apilayer.net/api/live";
    private static final String API_KEY_PARAM = "?access_key=";
    private static String API_KEY;
    private static final String FORMAT_PARAM = "&format=1";

    private SharedPreferences mSharedPreferences;

    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private TextView mLastUpdatedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mNavigationView = findViewById(R.id.nav_view_menu);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        mLastUpdatedView = findViewById(R.id.last_updated_view);

        initApiKey();
        String fullUrl = BASE_URL + API_KEY_PARAM + API_KEY + FORMAT_PARAM;

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // For testing purposes
                Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                // For testing purposes
                mDrawerLayout.closeDrawers();
                return false;
            }
        });

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
                        checkForLastUpdate();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getLocalizedMessage());
                // First time launching?
                //      Display error about not being able to fetch exchange rates from cloud.
                //      This should be done via a fragment with fragment_no_internet layout
                // Else:
                //      Proceed with current values in SharedPreferences
                checkForLastUpdate();
            }
        });

        // Add the request to the RequestQueue.
        volleyRequestQueue.add(stringRequest);

        /* Try this on July 31st
        for (int i = 0; i < 100; i++) {
            volleyRequestQueue.add(stringRequest);
        }
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                // For testing purposes
                Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
                // For testing purposes
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Checks when the exchange rate data was last updated to display in the navigation footer.
     * If the data doesn't exists, the footer is hidden and the content frame displays a
     * network-issue message.
     */
    private void checkForLastUpdate() {
        long timestamp = mSharedPreferences.getLong("timestamp", 0L);
        long timestampInMillis = timestamp * 1000L;
        if (timestamp != 0L) {
            Date date = new Date(timestampInMillis);
            java.text.SimpleDateFormat simpleDateFormat =
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            mLastUpdatedView.setText(getString(R.string.last_update, simpleDateFormat.format(date)));
        } else {
            // change to content frame to network issue message
            Fragment noInternetFragment = new NoInternetFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.content_frame, noInternetFragment, "no_internet_fragment");
            fragmentTransaction.commit();
        }
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
