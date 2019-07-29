package com.nicoqueijo.android.currencyconverter.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.dialogs.LanguageDialog;
import com.nicoqueijo.android.currencyconverter.dialogs.ThemeDialog;
import com.nicoqueijo.android.currencyconverter.fragments.ActiveCurrenciesFragment;
import com.nicoqueijo.android.currencyconverter.fragments.ConnectionErrorFragment;
import com.nicoqueijo.android.currencyconverter.fragments.ErrorFragment;
import com.nicoqueijo.android.currencyconverter.fragments.LoadingCurrenciesFragment;
import com.nicoqueijo.android.currencyconverter.fragments.SelectableCurrenciesFragment;
import com.nicoqueijo.android.currencyconverter.fragments.SourceCodeFragment;
import com.nicoqueijo.android.currencyconverter.fragments.VolleyErrorFragment;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;
import com.nicoqueijo.android.currencyconverter.interfaces.ICommunicator;
import com.nicoqueijo.android.currencyconverter.models.ApiEndpoint;
import com.nicoqueijo.android.currencyconverter.models.Currency;
import com.nicoqueijo.android.currencyconverter.room.AllCurrency;
import com.nicoqueijo.android.currencyconverter.room.AllCurrencyDao;
import com.nicoqueijo.android.currencyconverter.room.CurrencyDatabase;
import com.nicoqueijo.android.currencyconverter.singletons.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

/**
 * Main activity of the app. Hosts the app's Fragments and navigation drawer.
 * Implements ICommunicator to send data between Fragments.
 */
public class MainActivity extends AppCompatActivity implements ICommunicator {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static String sharedPrefsPropertiesFilename;

    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private SharedPreferences mSharedPrefsProperties;
    private InterstitialAd mInterstitialAd;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    public DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private MenuItem mLastUpdatedView;
    private Toast mCloseAppToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSharedPrefs();
        setLocaleAndTheme();
        setContentView(R.layout.activity_main);
        initAds();
        setTitle(R.string.app_name);
        initViews();
        appLaunchSetup();
    }

    /**
     * Initializes the ad configurations.
     */
    private void initAds() {
        initBannerAd();
        initInterstitialAd();
    }

    /**
     * Initializes the banner ad configurations.
     */
    private void initBannerAd() {
        MobileAds.initialize(this, getResources().getString(R.string.app_id));
        AdView adView = findViewById(R.id.banner_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    /**
     * Initializes the interstitial ad configurations.
     */
    private void initInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.ad_unit_id_interstitial_test));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd = null;
            }
        });
    }

    /**
     * Initializes the SharedPreferences object.
     */
    private void initSharedPrefs() {
        sharedPrefsPropertiesFilename = getPackageName().concat(".properties");
        mSharedPrefsProperties = getSharedPreferences(sharedPrefsPropertiesFilename, MODE_PRIVATE);
    }

    /**
     * Sets the locale (language) and theme based on the saved settings the user has.
     */
    private void setLocaleAndTheme() {
        String deviceLanguage = Locale.getDefault().getLanguage();
        setLocale(mSharedPrefsProperties.getString("language", deviceLanguage));
        setTheme(mSharedPrefsProperties.getInt("theme", ThemeDialog.Theme.LIGHT.getTheme()));
    }

    /**
     * Initializes the views.
     */
    @SuppressLint("ShowToast")
    private void initViews() {
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
                        return processNavItemSelection(menuItem);
                    }
                });

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                hideKeyboard();
            }
        };
    }

    /**
     * Handles the click operation of the Navigation Drawer by first closing the Drawer if it was
     * open and then choosing which method to call based on which menu item was clicked.
     *
     * @param menuItem the selected item
     * @return true to display the item as the selected item
     */
    private boolean processNavItemSelection(MenuItem menuItem) {
        mDrawerLayout.closeDrawers();
        boolean selected = false;
        switch (menuItem.getItemId()) {
            case R.id.nav_item_convert:
                selected = processNavItemConvert(menuItem);
                break;
            case R.id.nav_item_source_code:
                selected = processNavItemSourceCode(menuItem);
                break;
            case R.id.nav_item_language:
                selected = processNavItemLanguage();
                break;
            case R.id.nav_item_theme:
                selected = processNavItemTheme();
                break;
            case R.id.nav_item_share:
                selected = processNavItemShare();
                break;
            case R.id.nav_item_rate_app:
                selected = processNavItemRateApp();
                break;
            case R.id.nav_item_contact_us:
                selected = processNavItemContactUs();
                break;
        }
        return selected;
    }

    /**
     * If this menu item is checked it means the visible Fragment is either the active currencies
     * Fragment or a Fragment displaying an error. If the former, we can just return, if the latter
     * we still return but show a Snackbar first notifying no internet connection. The other scenario
     * is that the SourceCodeFragment is visible. In this case we hide that Fragment and show this
     * Fragment.
     *
     * @param menuItem the selected item
     * @return true to display the item as the selected item
     */
    private boolean processNavItemConvert(MenuItem menuItem) {
        FragmentTransaction fragmentTransaction;
        Fragment visibleFragment = getVisibleFragment();
        if (menuItem.isChecked()) {
            if (visibleFragment instanceof ErrorFragment) {
                ((ErrorFragment) visibleFragment).showNoInternetSnackbar();
            }
            return true;
        }
        fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.hide(visibleFragment);
        fragmentTransaction.show(mFragmentManager.findFragmentByTag(ActiveCurrenciesFragment.TAG));
        fragmentTransaction.commit();
        return true;
    }

    /**
     * Shows an interstitial ad if the user hasn't seen one for this button click. If this menu
     * item is checked it means that its Fragment is visible so we can return. If the visible
     * Fragment is an error Fragment we show a Snackbar notifying no internet connection. Otherwise
     * we hide the visible Fragment and create and show this Fragment.
     *
     * @param menuItem the selected item
     * @return true to display the item as the selected item
     */
    private boolean processNavItemSourceCode(MenuItem menuItem) {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        if (menuItem.isChecked()) {
            return true;
        }
        FragmentTransaction fragmentTransaction;
        Fragment visibleFragment = getVisibleFragment();
        if (visibleFragment instanceof ErrorFragment) {
            ((ErrorFragment) visibleFragment).showNoInternetSnackbar();
            return false;
        }
        fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.hide(visibleFragment);
        Fragment sourceCodeFragment = mFragmentManager.findFragmentByTag(SourceCodeFragment.TAG);
        if (sourceCodeFragment == null) {
            sourceCodeFragment = SourceCodeFragment.newInstance();
            fragmentTransaction.add(R.id.content_frame, sourceCodeFragment, SourceCodeFragment.TAG);
        } else {
            fragmentTransaction.show(sourceCodeFragment);
        }
        fragmentTransaction.commit();
        return true;
    }

    /**
     * Create and open the language chooser Dialog.
     *
     * @return false because we are merely triggering a Dialog and not changing the content frame
     * with another Fragment.
     */
    private boolean processNavItemLanguage() {
        DialogFragment languageDialog = LanguageDialog.newInstance();
        languageDialog.show(mFragmentManager, LanguageDialog.TAG);
        return false;
    }

    /**
     * Create and open the theme chooser Dialog.
     *
     * @return false because we are merely triggering a Dialog and not changing the content frame
     * with another Fragment.
     */
    private boolean processNavItemTheme() {
        DialogFragment themeDialog = ThemeDialog.newInstance();
        themeDialog.show(mFragmentManager, ThemeDialog.TAG);
        return false;
    }

    /**
     * Start an implicit Intent for the user to share the app via a chooser.
     *
     * @return false because we are merely triggering an Intent and not changing the content frame
     * with another Fragment.
     */
    private boolean processNavItemShare() {
        final String packageName = getPackageName();
        final String googlePlayWebUrl = "https://play.google.com/store/apps/details?id=";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String googlePlayLink = googlePlayWebUrl + packageName;
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, googlePlayLink);
        Intent chooser = Intent.createChooser(shareIntent, getString(R.string.share_via));
        startActivity(chooser);
        return false;
    }

    /**
     * Start an explicit intent to open the app's Google Play link in the device's Google Play app.
     * If this device doesn't have the Google Play app installed delegate the intent to a browser.
     *
     * @return false because we are merely triggering an Intent and not changing the content frame
     * with another Fragment.
     */
    private boolean processNavItemRateApp() {
        final String packageName = getPackageName();
        final String googlePlayMarketUrl = "market://details?id=";
        final String googlePlayWebUrl = "https://play.google.com/store/apps/details?id=";
        Intent rateAppIntent = new Intent(Intent.ACTION_VIEW);
        rateAppIntent.setData(Uri.parse(googlePlayMarketUrl + packageName));
        try {
            startActivity(rateAppIntent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayWebUrl
                    + packageName));
            startActivity(intent);
        }
        return false;
    }

    /**
     * Start an implicit Intent for the user to contact the developer via an email app. The subject
     * is prefilled with the app's name and the body is prefilled with the device manufacturer,
     * model, and the Android API level.
     *
     * @return false because we are merely triggering an Intent and not changing the content frame
     * with another Fragment.
     */
    private boolean processNavItemContactUs() {
        final String[] developerEmail = new String[]{"queijonicolas@gmail.com"};
        final String deviceInfo = "Device info:";
        final String deviceManufacturer = Build.MANUFACTURER;
        final String deviceModel = Build.MODEL;
        final String androidVersion = "Android version " + Build.VERSION.SDK_INT;
        final String emailTemplate = "\n\n\n" + deviceInfo + "\n  " + deviceManufacturer +
                " " + deviceModel + "\n  " + androidVersion;
        Intent contactUsIntent = new Intent(Intent.ACTION_SENDTO);
        contactUsIntent.setData(Uri.parse("mailto:"));
        contactUsIntent.putExtra(Intent.EXTRA_EMAIL, developerEmail);
        contactUsIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        contactUsIntent.putExtra(Intent.EXTRA_TEXT, emailTemplate);
        Intent chooser = Intent.createChooser(contactUsIntent, getString(R.string
                .select_email_app));
        startActivity(chooser);
        return false;
    }

    /**
     * Upon back button press, instead of directly destroying the activity, it first closes the
     * navigation drawer if it is open, it then pops the Fragment backstack if not empty, and
     * finally it asks the user to confirm they want to close the app in case they pressed the
     * back button accidentally.
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStack();
        } else if (!mCloseAppToast.getView().isShown()) {
            mCloseAppToast.show();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Gets called upon device configuration change like rotation. Overriding so the keyboard
     * doesn't pop up when device is rotated.
     *
     * @param newConfig unused but required.
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
     * <p>
     * If the activity was already created this means the user merely just changed the language or
     * theme and recreate() method was called as a result. Since the Fragments are already in place
     * we can exit.
     * <p>
     * Else, we place a splash screen Fragment on the content frame while we decide how to further
     * proceed.
     * If we have internet connection and our exchange rate data has not been
     * updated for at least 24 hours then we make an API call to get fresh data. The method
     * responsible for making the API call replaces this Fragment with an appropriate Fragment
     * depending on the result of the call.
     * If our exchange rate data has been updated within the past 24 hours then we simply load
     * the Fragment that displays them with their current local values.
     * In the case that we have no internet we do the following. If we have local exchange rate
     * values then we load a Fragment to display them. If we don't have any values locally then we
     * load a Fragment that notifies the user that we have no internet connection.
     */
    public void appLaunchSetup() {
        long timeElapsedSinceLastUpdate = checkForLastUpdate();
        if (activityHasExistingData()) {
            return;
        }
        final long emptySharedPrefs = -1L;
        final long twentyFourHours = 86400000L;
        Fragment loadingExchangeRatesFragment = LoadingCurrenciesFragment.newInstance();
        replaceFragment(loadingExchangeRatesFragment, LoadingCurrenciesFragment.TAG);
        if (Utility.isNetworkAvailable(this)) {
            if (timeElapsedSinceLastUpdate > twentyFourHours ||
                    timeElapsedSinceLastUpdate == emptySharedPrefs) {
                makeApiCall();
            } else {
                Fragment activeCurrenciesFragment = ActiveCurrenciesFragment.newInstance();
                replaceFragment(activeCurrenciesFragment, ActiveCurrenciesFragment.TAG);
            }
        } else {
            if (timeElapsedSinceLastUpdate != emptySharedPrefs) {
                Fragment activeCurrenciesFragment = ActiveCurrenciesFragment.newInstance();
                replaceFragment(activeCurrenciesFragment, ActiveCurrenciesFragment.TAG);
            } else {
                Fragment connectionErrorFragment = ConnectionErrorFragment.newInstance();
                replaceFragment(connectionErrorFragment, ConnectionErrorFragment.TAG);
            }
        }
    }

    /**
     * Checks when the exchange rate data was last updated to display in the navigation footer.
     *
     * @return time in milliseconds since the exchange rates were last updated or -1 if it was
     * never updated.
     */
    private long checkForLastUpdate() {
        long currentTime = System.currentTimeMillis();
        long timestamp = mSharedPrefsProperties.getLong("timestamp", 0L) * 1000;
        if (timestamp != 0L) {
            Date date = new Date(timestamp);
            java.text.SimpleDateFormat simpleDateFormat =
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            mLastUpdatedView.setTitle(getString(R.string.last_update,
                    simpleDateFormat.format(date)));
            return (currentTime - timestamp);
        }
        return -1L;
    }

    /**
     * Extracts the timestamp from the JSON object received from the API call and saves it locally
     * using SharedPrefs.
     *
     * @param jsonObject the JSON object containing the timestamp.
     * @throws JSONException in case a key being fetched doesn't exist.
     */
    private void persistTimestamp(JSONObject jsonObject) throws JSONException {
        SharedPreferences.Editor mSharedPrefsEditor = mSharedPrefsProperties.edit();
        long timestamp = jsonObject.getLong("timestamp");
        mSharedPrefsEditor.putLong("timestamp", timestamp);
        mSharedPrefsEditor.apply();
    }

    /**
     * Extracts the exchange rates from the JSON object received from the API call using the GSON
     * library and saves them locally using an SQLite database. Deletes everything in the table
     * first because apparently the insert statement doesn't replace existing values with new data.
     *
     * @param jsonObject the JSON object containing the exchange rates.
     */
    private void persistExchangeRates(JSONObject jsonObject) {
        Gson gson = new Gson();
        ApiEndpoint apiEndpoint = gson.fromJson(jsonObject.toString(), ApiEndpoint.class);
        apiEndpoint.getRates().currenciesToList();

        CurrencyDatabase currencyDatabase = CurrencyDatabase.getInstance(this);
        AllCurrencyDao allCurrencyDao = currencyDatabase.getAllCurrencyDao();
        allCurrencyDao.deleteAll();
        for (Currency currency : apiEndpoint.getRates().getCurrencies()) {
            allCurrencyDao.insert(new AllCurrency(currency.getCurrencyCode(), currency.getExchangeRate()));
        }

        /*try {
            ContentValues contentValues = new ContentValues();
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            SQLiteDatabase database = databaseHelper.getWritableDatabase();
            database.execSQL("DELETE FROM " + EntryAllCurrencies.TABLE_NAME);
            database.beginTransaction();
            for (Currency currency : apiEndpoint.getRates().getCurrencies()) {
                contentValues.put(EntryAllCurrencies.COLUMN_CURRENCY_CODE,
                        currency.getCurrencyCode());
                contentValues.put(EntryAllCurrencies.COLUMN_CURRENCY_VALUE,
                        currency.getExchangeRate());
                database.insert(EntryAllCurrencies.TABLE_NAME, null, contentValues);
            }
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Gets a random API key from a set of API keys.
     *
     * @return an API key
     */
    private String getApiKey() {
        String[] apiKeys = getResources().getStringArray(R.array.api_keys);
        int random = new Random().nextInt(apiKeys.length);
        return apiKeys[random];
    }

    /**
     * Replaces whatever Fragment is inside the content frame (if any) with the Fragment being
     * passed.
     *
     * @param fragment the Fragment to replace the content frame with.
     * @param tag      identifier for this transaction.
     */
    private void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment, tag);
        fragmentTransaction.commit();
    }

    /**
     * Sets the locale to a new language.
     *
     * @param lang the new language to set the app to.
     */
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(myLocale);
        resources.updateConfiguration(configuration, displayMetrics);
    }

    /**
     * Used to pass a currency object between Fragments.
     *
     * @param currency the currency object being passed.
     */
    @Override
    public void passSelectedCurrency(Currency currency) {
        ActiveCurrenciesFragment activeCurrenciesFragment = (ActiveCurrenciesFragment)
                mFragmentManager.findFragmentByTag(ActiveCurrenciesFragment.TAG);
        activeCurrenciesFragment.addActiveCurrency(currency);
    }

    /**
     * Returns the Fragment that is currently visible to the user. Does this by iterating through
     * the active Fragments and determining which one is in memory and is visible. If this is the
     * SelectableCurrenciesFragment we remove it and pop it from the stack since this Fragment is
     * really a supplement to the ActiveCurrenciesFragment.
     *
     * @return the Fragment currently being displayed or null of no Fragments in manager.
     */
    private Fragment getVisibleFragment() {
        List<Fragment> fragments = mFragmentManager.getFragments();
        Fragment visibleFragment = null;
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible()) {
                if (fragment instanceof SelectableCurrenciesFragment) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right);
                    fragmentTransaction.remove(fragment);
                    mFragmentManager.popBackStack();
                    fragmentTransaction.commit();
                    continue;
                }
                visibleFragment = fragment;
            }
        }
        return visibleFragment;
    }

    /**
     * Determines if the app is at a state where it contains data the user can interact with. This
     * is true when there is a Fragment present in the content frame and that Fragment is not an
     * error Fragment.
     *
     * @return true if method description holds.
     */
    private boolean activityHasExistingData() {
        return mFragmentManager.findFragmentById(R.id.content_frame) != null &&
                !(mFragmentManager.findFragmentById(R.id.content_frame) instanceof ErrorFragment);
    }

    /**
     * Initializes the Volley request queue, makes the API call, and adds the request to volley's
     * request queue.
     */
    private void makeApiCall() {
        RequestQueue volleyRequestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        StringRequest stringRequest = initVolleyStringRequest();
        volleyRequestQueue.add(stringRequest);
    }

    /**
     * Attempts to request a string response from the provided URL.
     * If a response is received we first check if an error wasn't returned. If it wasn't we
     * can update our local exchange rate values and load the Fragment that performs the
     * conversions. If we received an error from the request we extract the error message from
     * the response and load a Fragment that shows the user information about the error.
     *
     * @return the string request for retrieving the response body at the given URL.
     */
    private StringRequest initVolleyStringRequest() {
        String url = Uri.parse("http://openexchangerates.org/api/latest.json")
                .buildUpon().appendQueryParameter("app_id", getApiKey()).build().toString();
        StringRequest stringRequest;
        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.has("error");
                            if (!error) {
                                persistTimestamp(jsonObject);
                                persistExchangeRates(jsonObject);
                                checkForLastUpdate();
                                Fragment activeCurrenciesFragment = ActiveCurrenciesFragment
                                        .newInstance();
                                replaceFragment(activeCurrenciesFragment, ActiveCurrenciesFragment
                                        .TAG);
                            } else {
                                final int indentSpaces = 4;
                                JSONObject errorDescription = jsonObject.getJSONObject("description");
                                String errorMessage = errorDescription.toString(indentSpaces);
                                Fragment volleyErrorFragment = VolleyErrorFragment
                                        .newInstance(errorMessage);
                                replaceFragment(volleyErrorFragment, VolleyErrorFragment.TAG);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = error.toString();
                Fragment errorFragment = VolleyErrorFragment.newInstance(errorMessage);
                replaceFragment(errorFragment, VolleyErrorFragment.TAG);
            }
        });
        return stringRequest;
    }
}
