package com.nicoqueijo.android.currencyconverter.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

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
    private String apiFullUrl;

    private SharedPreferences mSharedPreferences;

    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private TextView mLastUpdatedView;

    private FragmentManager fragmentManager = getSupportFragmentManager();

    private RequestQueue volleyRequestQueue;
    private StringRequest stringRequest;

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
        apiFullUrl = BASE_URL + API_KEY_PARAM + API_KEY + FORMAT_PARAM;
        appLaunchSetup();

        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                        // For testing purposes
                        Snackbar.make(findViewById(R.id.content_frame),
                                menuItem.getTitle(), Snackbar.LENGTH_SHORT).show();
                        // For testing purposes

                        mDrawerLayout.closeDrawers();
                        return false;
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_menu, menu);
        final ImageView refreshMenuItem = (ImageView) menu.findItem(R.id.refresh).getActionView();
        refreshMenuItem.setImageResource(R.drawable.ic_refresh);
        refreshMenuItem.setPadding(24, 24, 24, 24);
        refreshMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processRefreshClick(refreshMenuItem);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void appLaunchSetup() {
        if (isNetworkAvailable()) {
            volleyRequestQueue = Volley.newRequestQueue(this);
            initVolleyStringRequest();
            volleyRequestQueue.add(stringRequest);
        } else if (!isSharedPreferencesEmpty()) {
            checkForLastUpdate();
            // Restore contents of RecyclerView
        } else {
            fragmentManager.beginTransaction().add(R.id.content_frame,
                    new NoInternetFragment(), "no_internet_fragment").commit();
            Snackbar.make(findViewById(R.id.content_frame),
                    R.string.no_internet, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void processRefreshClick(ImageView menuItem) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if (isNetworkAvailable()) {
            menuItem.startAnimation(AnimationUtils
                    .loadAnimation(MainActivity.this, R.anim.rotate));
            volleyRequestQueue = Volley.newRequestQueue(this);
            initVolleyStringRequest();
            volleyRequestQueue.add(stringRequest);
        } else {
            Snackbar.make(findViewById(R.id.content_frame),
                    R.string.no_internet, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks when the exchange rate data was last updated to display in the navigation footer.
     */
    private void checkForLastUpdate() {
        long timestamp = mSharedPreferences.getLong("timestamp", 0L);
        long timestampInMillis = timestamp * 1000L;
        Date date = new Date(timestampInMillis);
        java.text.SimpleDateFormat simpleDateFormat =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        mLastUpdatedView.setText(getString(R.string.last_update,
                simpleDateFormat.format(date)));
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

    /**
     * Checks weather there is currently an active internet connection.
     *
     * @return whether there is an internet connection.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void initVolleyStringRequest() {
        // Request a string response from the provided URL.
        stringRequest = new StringRequest(Request.Method.GET, apiFullUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                updateSharedPreferencesExchangeRates(jsonObject);
                                checkForLastUpdate();
                                // Go on with the app after updating rates (Populating RecyclerView)
                            } else if (!isSharedPreferencesEmpty()) {
                                checkForLastUpdate();
                                // Go on with the app using existing rates (Populating RecyclerView)
                            } else {
                                JSONObject error = jsonObject.getJSONObject("error");
                                final int INDENT_SPACES = 4;
                                // To be displayed in a "Show more" View to supplement
                                // a generic error message in the content frame.
                                String errorDetails = error.toString(INDENT_SPACES);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // First time launching?
                //      Display error about not being able to fetch exchange rates from cloud.
                //      This should be done via a fragment with fragment_no_internet layout
                // Else:
                //      Proceed with current values in SharedPreferences
            }
        });
    }
}
