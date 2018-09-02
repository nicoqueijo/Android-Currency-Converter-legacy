package com.nicoqueijo.android.currencyconverter.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.fragments.ActiveExchangeRatesFragment;
import com.nicoqueijo.android.currencyconverter.fragments.ErrorFragment;
import com.nicoqueijo.android.currencyconverter.fragments.LoadingExchangeRatesFragment;
import com.nicoqueijo.android.currencyconverter.fragments.NoInternetFragment;
import com.nicoqueijo.android.currencyconverter.helpers.Constants;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;
import com.nicoqueijo.android.currencyconverter.interfaces.ICommunicator;
import com.nicoqueijo.android.currencyconverter.models.Currency;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

/**
 * Main activity of the app. Hosts the app's fragments and navigation drawer.
 * Implements ICommunicator to send data between fragments.
 */
public class MainActivity extends AppCompatActivity implements ICommunicator {

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final String BASE_URL = "http://apilayer.net/api/live";
    private static final String API_KEY_PARAM = "?access_key=";
    private static String apiKey;
    private static final String FORMAT_PARAM = "&format=1";
    private String apiFullUrl;

    public static String sharedPrefsRatesFilename;
    public static String sharedPrefsTimeFilename;
    private SharedPreferences mSharedPreferencesRates;
    private SharedPreferences mSharedPreferencesTime;

    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private TextView mLastUpdatedView;
    private Toast mCloseAppToast;

    private FragmentManager fragmentManager = getSupportFragmentManager();

    private RequestQueue volleyRequestQueue;
    private StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefsRatesFilename = getPackageName().concat(".rates");
        sharedPrefsTimeFilename = getPackageName().concat(".time");
        mSharedPreferencesRates = getSharedPreferences(sharedPrefsRatesFilename, MODE_PRIVATE);
        mSharedPreferencesTime = getSharedPreferences(sharedPrefsTimeFilename, MODE_PRIVATE);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mNavigationView = findViewById(R.id.nav_view_menu);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        initListeners();
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        mLastUpdatedView = findViewById(R.id.last_updated_view);
        mCloseAppToast = Toast.makeText(this, R.string.tap_to_close, Toast.LENGTH_SHORT);

        initApiKey();
        apiFullUrl = BASE_URL + API_KEY_PARAM + apiKey + FORMAT_PARAM;
        appLaunchSetup();
    }

    private void initListeners() {
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        Snackbar.make(fragmentManager.getFragments().get(0).getView(),
                                menuItem.getTitle(), Snackbar.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        return false;
                    }
                });

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_app, menu);
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
        } else if (mCloseAppToast.getView().isShown()) {
            super.onBackPressed();
        } else {
            mCloseAppToast.show();
        }
    }

    private void appLaunchSetup() {
        long timeOfLastUpdate = checkForLastUpdate();
        if (isNetworkAvailable()) {
            if (timeOfLastUpdate > Constants.TWELVE_HOURS ||
                    timeOfLastUpdate == Constants.EMPTY_SHARED_PREFS) {
                if (fragmentManager.findFragmentByTag(LoadingExchangeRatesFragment.TAG) == null) {
                    Fragment loadingExchangeRatesFragment = LoadingExchangeRatesFragment.newInstance();
                    fragmentManager.beginTransaction().replace(R.id.content_frame,
                            loadingExchangeRatesFragment, LoadingExchangeRatesFragment.TAG).commit();
                    makeApiCall();
                }
            } else {
                if (fragmentManager.findFragmentByTag(ActiveExchangeRatesFragment.TAG) == null) {
                    Fragment activeExchangeRatesFragment = ActiveExchangeRatesFragment.newInstance();
                    fragmentManager.beginTransaction().replace(R.id.content_frame,
                            activeExchangeRatesFragment, ActiveExchangeRatesFragment.TAG).commit();
                }
            }
        } else {
            if (timeOfLastUpdate != Constants.EMPTY_SHARED_PREFS) {
                if (fragmentManager.findFragmentByTag(ActiveExchangeRatesFragment.TAG) == null) {
                    Fragment activeExchangeRatesFragment = ActiveExchangeRatesFragment.newInstance();
                    fragmentManager.beginTransaction().replace(R.id.content_frame,
                            activeExchangeRatesFragment, ActiveExchangeRatesFragment.TAG).commit();
                }
            } else {
                Fragment noInternetFragment = NoInternetFragment.newInstance();
                fragmentManager.beginTransaction().replace(R.id.content_frame,
                        noInternetFragment, NoInternetFragment.TAG).commit();
            }
        }
    }

    private void processRefreshClick(ImageView menuItem) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if (isNetworkAvailable()) {
            Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
            menuItem.startAnimation(animRotate);
            if (fragmentManager.findFragmentByTag(ActiveExchangeRatesFragment.TAG) == null) {
                Fragment activeExchangeRatesFragment = ActiveExchangeRatesFragment.newInstance();
                fragmentManager.beginTransaction().replace(R.id.content_frame,
                        activeExchangeRatesFragment, ActiveExchangeRatesFragment.TAG).commit();
                makeApiCall();
            }
        } else {
            showNoInternetSnackbar();
        }
    }

    private void makeApiCall() {
        volleyRequestQueue = Volley.newRequestQueue(this);
        initVolleyStringRequest();
        volleyRequestQueue.add(stringRequest);
    }

    private void showNoInternetSnackbar() {
        List<Fragment> fragmentList = fragmentManager.getFragments();
        Fragment activeFragment = fragmentList.get(0);
        Snackbar.make(activeFragment.getView(), R.string.no_internet, Snackbar.LENGTH_SHORT).show();
    }


    /**
     * Checks when the exchange rate data was last updated to display in the navigation footer.
     *
     * @return time in milliseconds since the exchange rates were last updated or -1 if it was
     * never updated.
     */
    private long checkForLastUpdate() {
        long currentTime = System.currentTimeMillis();
        long timestamp = mSharedPreferencesTime.getLong("timestamp", 0L) * 1000;
        if (timestamp != 0L) {
            Date date = new Date(timestamp);
            java.text.SimpleDateFormat simpleDateFormat =
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            mLastUpdatedView.setText(getString(R.string.last_update, simpleDateFormat.format(date)));
            return (currentTime - timestamp);
        }
        return -1L;
    }

    /**
     * Extracts the timestamp and exchange rates from the JSON object and saves them locally via
     * SharedPreferences. It skips over few exchange rates that are not of interest; e.g. silver.
     *
     * @param jsonObject the JSON object containing the exchange rates and timestamp.
     * @throws JSONException in case a key being fetched doesn't exist.
     */
    private void updateSharedPreferencesExchangeRates(JSONObject jsonObject) throws JSONException {
        SharedPreferences.Editor mSharedPreferencesEditor = mSharedPreferencesTime.edit();
        long timestamp = jsonObject.getLong("timestamp");
        mSharedPreferencesEditor.putLong("timestamp", timestamp);
        mSharedPreferencesEditor.apply();
        mSharedPreferencesEditor = mSharedPreferencesRates.edit();
        Set<String> exclusionList = new HashSet<>(Arrays.asList(getResources()
                .getStringArray(R.array.exclusion_list)));
        JSONObject rates = jsonObject.getJSONObject("quotes");
        JSONArray keys = rates.names();
        for (int i = 0; i < keys.length(); i++) {
            String key = keys.getString(i);
            if (exclusionList.contains(key)) {
                continue;
            }
            double value = rates.getDouble(key);
            Utility.putDouble(mSharedPreferencesEditor, key, value);
        }
        mSharedPreferencesEditor.apply();
    }

    /**
     * Initializes the API key from a local private file that is not tracked by Git for obvious
     * reasons.
     */
    private void initApiKey() {
        apiKey = getResources().getString(R.string.api_key);
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
                                Fragment activeExchangeRatesFragment = ActiveExchangeRatesFragment
                                        .newInstance();
                                fragmentManager.beginTransaction().replace(R.id.content_frame,
                                        activeExchangeRatesFragment,
                                        ActiveExchangeRatesFragment.TAG).commit();
                            } else {
                                JSONObject error = jsonObject.getJSONObject("error");
                                String errorMessage = error.toString(Constants.INDENT_SPACES);
                                Fragment errorFragment = ErrorFragment.newInstance(errorMessage);
                                fragmentManager.beginTransaction().replace(R.id.content_frame,
                                        errorFragment, ErrorFragment.TAG).commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = error.getMessage();
                Fragment errorFragment = ErrorFragment.newInstance(errorMessage);
                fragmentManager.beginTransaction().replace(R.id.content_frame,
                        errorFragment, ErrorFragment.TAG).commit();
            }
        });
    }

    @Override
    public void passSelectedCurrency(Currency currency) {
        ActiveExchangeRatesFragment activeExchangeRatesFragment = (ActiveExchangeRatesFragment)
                fragmentManager.findFragmentByTag(ActiveExchangeRatesFragment.TAG);
        activeExchangeRatesFragment.addActiveCurrency(currency);
    }
}
