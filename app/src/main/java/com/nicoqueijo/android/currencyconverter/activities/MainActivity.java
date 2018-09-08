package com.nicoqueijo.android.currencyconverter.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.nicoqueijo.android.currencyconverter.fragments.SelectExchangeRatesFragment;
import com.nicoqueijo.android.currencyconverter.fragments.SourceCodeFragment;
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

    private static final String API_BASE_URL = "http://apilayer.net/api/live";
    private static final String API_KEY_PARAM = "?access_key=";
    private static String apiKey;
    private static final String API_FORMAT_PARAM = "&format=1";
    private String apiFullUrl;

    public static String sharedPrefsRatesFilename;
    public static String sharedPrefsTimeFilename;
    private SharedPreferences mSharedPreferencesRates;
    private SharedPreferences mSharedPreferencesTime;

    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    public DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private MenuItem mLastUpdatedView;
    private Toast mCloseAppToast;

    private FragmentManager fragmentManager = getSupportFragmentManager();

    private RequestQueue volleyRequestQueue;
    private StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewsAndSharedPrefs();
        initApiKey();
        apiFullUrl = API_BASE_URL + API_KEY_PARAM + apiKey + API_FORMAT_PARAM;
        appLaunchSetup();
    }

    /**
     * Initializes the views and SharedPrefs.
     */
    private void initViewsAndSharedPrefs() {
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
        mLastUpdatedView = mNavigationView.getMenu().findItem(R.id.nav_item_last_updated);
        mCloseAppToast = Toast.makeText(this, R.string.tap_to_close, Toast.LENGTH_SHORT);
    }

    /**
     * Sets the listeners for navigation drawer.
     */
    private void initListeners() {
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        mDrawerLayout.closeDrawers();
                        // Delegate all this to a method(s) later //////////////////////////////////
                        final String GOOGLE_PLAY_WEB_URL = "https://play.google.com/store/apps/details?id=";
                        final String PACKAGE_NAME = getPackageName();
                        FragmentTransaction fragmentTransaction;
                        switch (menuItem.getItemId()) {
                            case R.id.nav_item_convert: {
                                Fragment fragment = fragmentManager.findFragmentByTag(ActiveExchangeRatesFragment.TAG);
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.hide(fragmentManager.findFragmentByTag(SourceCodeFragment.TAG));
                                if (fragment == null) {
                                    fragment = ActiveExchangeRatesFragment.newInstance();
                                    fragmentTransaction.add(R.id.content_frame, fragment, ActiveExchangeRatesFragment.TAG);
                                    fragmentTransaction.show(fragment);
                                } else {
                                    fragmentTransaction.show(fragment);
                                }
                                fragmentTransaction.commit();
                            }
                            break;
                            case R.id.nav_item_language: {

                            }
                            break;
                            case R.id.nav_item_theme: {

                            }
                            break;
                            case R.id.nav_item_source_code: {
                                Fragment fragment = fragmentManager.findFragmentByTag(SourceCodeFragment.TAG);
                                fragmentTransaction = fragmentManager.beginTransaction();
                                if (fragmentManager.findFragmentByTag(SelectExchangeRatesFragment.TAG) != null) {
                                    fragmentTransaction.remove(fragmentManager.findFragmentByTag(SelectExchangeRatesFragment.TAG));
                                }
                                fragmentTransaction.hide(fragmentManager.findFragmentByTag(ActiveExchangeRatesFragment.TAG));
                                if (fragment == null) {
                                    fragment = SourceCodeFragment.newInstance();
                                    fragmentTransaction.add(R.id.content_frame, fragment, SourceCodeFragment.TAG);
                                    fragmentTransaction.show(fragment);
                                } else {
                                    fragmentTransaction.show(fragment);
                                }
                                fragmentTransaction.commit();
                            }
                            break;
                            case R.id.nav_item_share: {
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                String googlePlayLink = GOOGLE_PLAY_WEB_URL + PACKAGE_NAME;
                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                                shareIntent.putExtra(Intent.EXTRA_TEXT, googlePlayLink);
                                startActivity(Intent.createChooser(shareIntent,
                                        getString(R.string.share_via)));
                            }
                            break;
                            case R.id.nav_item_rate_app: {
                                final String GOOGLE_PLAY_MARKET_URL = "market://details?id=";
                                Intent rateAppIntent = new Intent(Intent.ACTION_VIEW);
                                rateAppIntent.setData(Uri.parse(GOOGLE_PLAY_MARKET_URL + PACKAGE_NAME));
                                try {
                                    startActivity(rateAppIntent);
                                } catch (ActivityNotFoundException e) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_WEB_URL + PACKAGE_NAME));
                                    startActivity(intent);
                                }
                            }
                            break;
                            case R.id.nav_item_contact_us: {
                                final String[] DEVELOPER_EMAIL = new String[]{"queijonicolas@gmail.com"};
                                final String DEVICE_INFO = "Device info:";
                                final String DEVICE_MANUFACTURER = Build.MANUFACTURER;
                                final String DEVICE_MODEL = Build.MODEL;
                                final String ANDROID_VERSION = "Android version " + Build.VERSION.SDK_INT;
                                final String EMAIL_TEMPLATE = "\n\n\n" + DEVICE_INFO + "\n  " + DEVICE_MANUFACTURER + " " + DEVICE_MODEL + "\n  " + ANDROID_VERSION;
                                Intent contactUsIntent = new Intent(Intent.ACTION_SENDTO);
                                contactUsIntent.setData(Uri.parse("mailto:"));
                                contactUsIntent.putExtra(Intent.EXTRA_EMAIL, DEVELOPER_EMAIL);
                                contactUsIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                                contactUsIntent.putExtra(Intent.EXTRA_TEXT, EMAIL_TEMPLATE);
                                Intent chooser = Intent.createChooser(contactUsIntent, getString(R.string.select_email_app));
                                startActivity(chooser);
                            }
                            break;
                        }
                        ////////////////////////////////////////////////////////////////////////////
                        return true;
                    }
                });

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
                            .getWindowToken(), 0);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_refresh, menu);
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

    /**
     * Upon back button press, instead of directly destroying the activity, it first closes the
     * navigation drawer if it is open, it then pops the fragment backstack if not empty, and
     * finally it asks the user to confirm they want to close the app in case they pressed the
     * back button accidentally.
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else if (!mCloseAppToast.getView().isShown()) {
            mCloseAppToast.show();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Gets called upon device configuration change like rotation. Overridind so the keyboard
     * doesn't pop up when device is rotated.
     *
     * @param newConfig unused
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hideKeyboard();
    }

    /**
     * Hides the keyboard if it's being shown.
     */
    private void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * This code section determines which Fragment gets loaded into the content frame when the
     * Activity is created.
     * If we have internet connection and our exchange rate data has not been
     * updated for at least six hours then we load a Fragment that shows a splash screen and make
     * an API call to get fresh data. The method responsible for making the API call replaces this
     * Fragment with an appropriate Fragment depending on the result of the call.
     * If our exchange rate data has been updated within the past six hours then we simply load the
     * Fragment that displays them with their current local values.
     * In the case that we have no internet we do the following. If we have local exchange rate
     * values then we load a Fragment to display them. If we don't have any values locally then we
     * load a Fragment that notifies the user that we have no internet connection.
     */
    private void appLaunchSetup() {
        final long EMPTY_SHARED_PREFS = -1L;
        final long TWELVE_HOURS = 43200000L;
        long timeOfLastUpdate = checkForLastUpdate();
        if (isNetworkAvailable()) {
            if (timeOfLastUpdate > TWELVE_HOURS ||
                    timeOfLastUpdate == EMPTY_SHARED_PREFS) {
                if (fragmentManager.findFragmentByTag(LoadingExchangeRatesFragment.TAG) == null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment loadingExchangeRatesFragment = LoadingExchangeRatesFragment
                            .newInstance();
                    fragmentTransaction.replace(R.id.content_frame, loadingExchangeRatesFragment,
                            LoadingExchangeRatesFragment.TAG);
                    fragmentTransaction.commit();
                    makeApiCall();
                }
            } else {
                if (fragmentManager.findFragmentByTag(ActiveExchangeRatesFragment.TAG) == null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment activeExchangeRatesFragment = ActiveExchangeRatesFragment
                            .newInstance();
                    fragmentTransaction.replace(R.id.content_frame, activeExchangeRatesFragment,
                            ActiveExchangeRatesFragment.TAG);
                    fragmentTransaction.commit();
                }
            }
        } else {
            if (timeOfLastUpdate != EMPTY_SHARED_PREFS) {
                if (fragmentManager.findFragmentByTag(ActiveExchangeRatesFragment.TAG) == null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment activeExchangeRatesFragment = ActiveExchangeRatesFragment
                            .newInstance();
                    fragmentTransaction.replace(R.id.content_frame, activeExchangeRatesFragment,
                            ActiveExchangeRatesFragment.TAG);
                    fragmentTransaction.commit();
                }
            } else {
                Fragment noInternetFragment = NoInternetFragment.newInstance();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, noInternetFragment,
                        NoInternetFragment.TAG);
                fragmentTransaction.commit();
            }
        }
    }

    /**
     * Attempts to refresh the exchange rate data if an internet connection is active and an error
     * had previously occurred. Otherwise it displays a Snackbar notifying there is no internet
     * connection.
     *
     * @param menuItem the ImageView of the refresh menu item in order to animate it.
     */
    private void processRefreshClick(ImageView menuItem) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if (isNetworkAvailable()) {
            Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
            menuItem.startAnimation(animRotate);
            Fragment activeFragment = fragmentManager.getFragments().get(0);
            if (activeFragment.getTag().equals(ErrorFragment.TAG) ||
                    activeFragment.getTag().equals(NoInternetFragment.TAG)) {
                Fragment loadingExchangeRatesFragment = LoadingExchangeRatesFragment.newInstance();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, loadingExchangeRatesFragment,
                        LoadingExchangeRatesFragment.TAG);
                fragmentTransaction.commit();
                makeApiCall();
            }
        } else {
            showNoInternetSnackbar();
        }
    }

    /**
     * Initializes the Volley request queue, makes the API call, and adds the request to the queue.
     */
    private void makeApiCall() {
        volleyRequestQueue = Volley.newRequestQueue(this);
        initVolleyStringRequest();
        volleyRequestQueue.add(stringRequest);
    }

    /**
     * Shows the Snackbar notifying there is no internet connection.
     */
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
            mLastUpdatedView.setTitle(getString(R.string.last_update, simpleDateFormat
                    .format(date)));
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

    /**
     * Attempts to request a string response from the provided URL.
     * If a response is received we first check if its "success" key returned true. If it did we
     * can update our local exchange rate values and load the Fragment that performs the
     * conversions. If false is returned or we receive an error from the request we extract the
     * error message from the response and load a Fragment that shows the user information about
     * the error.
     */
    private void initVolleyStringRequest() {
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
                                Fragment activeExchangeRatesFragment = ActiveExchangeRatesFragment
                                        .newInstance();
                                FragmentTransaction fragmentTransaction = fragmentManager
                                        .beginTransaction();
                                fragmentTransaction.replace(R.id.content_frame,
                                        activeExchangeRatesFragment,
                                        ActiveExchangeRatesFragment.TAG);
                                fragmentTransaction.commit();
                            } else {
                                final int INDENT_SPACES = 4;
                                JSONObject error = jsonObject.getJSONObject("error");
                                String errorMessage = error.toString(INDENT_SPACES);
                                Fragment errorFragment = ErrorFragment.newInstance(errorMessage);
                                FragmentTransaction fragmentTransaction = fragmentManager
                                        .beginTransaction();
                                fragmentTransaction.replace(R.id.content_frame, errorFragment,
                                        ErrorFragment.TAG);
                                fragmentTransaction.commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = error.toString();
                Fragment errorFragment = ErrorFragment.newInstance(errorMessage);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, errorFragment, ErrorFragment.TAG);
                fragmentTransaction.commit();
            }
        });
    }

    /**
     * Used to pass a currency object between Fragments.
     *
     * @param currency the currency object being passed.
     */
    @Override
    public void passSelectedCurrency(Currency currency) {
        ActiveExchangeRatesFragment activeExchangeRatesFragment = (ActiveExchangeRatesFragment)
                fragmentManager.findFragmentByTag(ActiveExchangeRatesFragment.TAG);
        activeExchangeRatesFragment.addActiveCurrency(currency);
    }
}
