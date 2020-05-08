package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.hasActiveCurrenciesNavigation
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.hideKeyboard
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity(), BillingProcessor.IBillingHandler {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var billingProcessor: BillingProcessor

    lateinit var drawer: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navController: NavController
    private lateinit var navView: NavigationView
    private lateinit var lastUpdateLabel: TextView
    private lateinit var closeAppToast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        initBillingProcessor()
        initViews()
        handleNavigation()
        initLastUpdateLabel()
    }

    private fun initBillingProcessor() {
        billingProcessor = BillingProcessor.newBillingProcessor(this, getString(R.string.google_play_license_key), this)
        billingProcessor.initialize()
    }

    override fun onBillingInitialized() {
        if (billingProcessor.isPurchased(MainActivityViewModel.REMOVE_ADS_PRODUCT_ID)) {
            navView.menu.findItem(R.id.removeAds).isVisible = false
            MainActivityViewModel.adsEnabled = false
        } else {
            initBannerAd()
        }
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {}

    override fun onPurchaseHistoryRestored() {}

    override fun onBillingError(errorCode: Int, error: Throwable?) {}

    /**
     * Called upon return from Google Billing dialog. Here we determine if purchase went through
     * and if so recreate the Activity to reflect the new state.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
        if (resultCode == Activity.RESULT_OK) {
            recreate()
        }
    }

    /**
     * The banners ad's view needs to be added programatically in order to set its ad unit id
     * at run time.
     * See: https://stackoverflow.com/a/34232962/5906793
     */
    private fun initBannerAd() {
        MobileAds.initialize(this, resources.getString(R.string.app_id))
        val adView = AdView(this)
        adView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        adView.adUnitId = viewModel.getBannerAdId(this)
        adView.adSize = AdSize.SMART_BANNER
        findViewById<LinearLayout>(R.id.main_canvas).addView(adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    @SuppressLint("ShowToast")
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.nav_drawer)
        initListeners()
        drawer.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navView = findViewById(R.id.nav_view)
        lastUpdateLabel = findViewById(R.id.last_updated_label)
        closeAppToast = Toast.makeText(this, R.string.tap_to_close, Toast.LENGTH_SHORT)
    }

    private fun handleNavigation() {
        navController = findNavController(R.id.content_frame)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            viewModel.activeFragment.postValue(destination.id)
            viewModel.fragmentBackstackEntries.add(destination.id)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            drawer.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                R.id.rateApp -> {
                    fireRateAppIntent()
                    false
                }
                R.id.tips -> {
                    fireTipsDialog()
                    false
                }
                R.id.removeAds -> {
                    billingProcessor.purchase(this, MainActivityViewModel.REMOVE_ADS_PRODUCT_ID)
                    false
                }
                else -> {
                    false
                }
            }
        }
    }

    /**
     * Start an explicit intent to open the app's Google Play link in the device's Google Play app.
     * If this device doesn't have the Google Play app installed delegate the intent to a browser.
     */
    private fun fireRateAppIntent() {
        val packageName = packageName
        val googlePlayMarketUrl = "market://details?id="
        val googlePlayWebUrl = "https://play.google.com/store/apps/details?id="
        val rateAppIntent = Intent(Intent.ACTION_VIEW)
        rateAppIntent.data = Uri.parse(googlePlayMarketUrl + packageName)
        try {
            startActivity(rateAppIntent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayWebUrl + packageName))
            startActivity(intent)
        }
    }

    private fun fireTipsDialog() {
        MaterialAlertDialogBuilder(this)
                .setView(R.layout.tips)
                .setBackground(ContextCompat.getDrawable(this, R.drawable.dialog_background))
                .show()
    }

    private fun initListeners() {
        actionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close) {

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                hideKeyboard()
            }
        }
    }

    private fun initLastUpdateLabel() {
        viewModel.activeFragment.observe(this, Observer { activeFragment ->
            when (activeFragment) {
                R.id.watchlistFragment ->
                    lastUpdateLabel.text = getString(R.string.last_update, viewModel.getFormattedLastUpdate())
            }
        })
    }

    private fun showNoInternetSnackbar() {
        Snackbar.make(findViewById(R.id.content_frame), R.string.no_internet, Snackbar.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else if (navController.hasActiveCurrenciesNavigation()) {
            viewModel.fragmentBackstackEntries.remove(navController.currentDestination?.id!!)
            navController.popBackStack()
        } else if (!closeAppToast.view.isShown) {
            closeAppToast.show()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        billingProcessor.release()
        super.onDestroy()
    }
}
