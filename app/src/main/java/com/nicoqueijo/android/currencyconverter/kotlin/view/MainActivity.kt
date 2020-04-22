package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.hasActiveCurrenciesNavigation
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.hideKeyboard
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.MainActivityViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel

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
        initBannerAd()
        initViews()
        handleNavigation()
        initLastUpdateLabel()
    }

    /**
     * The banners ad's view needs to be added programatically if we want to set its ad unit id
     * programatically.     See: https://stackoverflow.com/a/34232962/5906793
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
            when (viewModel.activeFragment.value) {
                R.id.activeCurrenciesFragment -> {
                    handleBackstack(menuItem.itemId, R.id.activeCurrenciesFragment)
                }
                R.id.sourceCodeFragment -> {
                    handleBackstack(menuItem.itemId, R.id.sourceCodeFragment)
                }
                R.id.selectableCurrenciesFragment -> {
                    if (menuItem.itemId == R.id.activeCurrenciesFragment) {
                        true
                    } else {
                        navController.navigate(menuItem.itemId)
                        true
                    }
                }
                R.id.errorFragment -> {
                    showNoInternetSnackbar()
                    false
                }
                R.id.loadingCurrenciesFragment -> false
                else -> {
                    false
                }
            }
        }
    }

    private fun handleBackstack(menuItemId: Int, activeFragment: Int): Boolean {
        return when {
            menuItemId == activeFragment -> {
                true
            }
            viewModel.fragmentBackstackEntries.contains(menuItemId) -> {
                navController.popBackStack(menuItemId, false)
                viewModel.fragmentBackstackEntries.remove(activeFragment)
                true
            }
            else -> {
                navController.navigate(menuItemId)
                viewModel.fragmentBackstackEntries.add(menuItemId)
                true
            }
        }
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
                R.id.activeCurrenciesFragment ->
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
}
