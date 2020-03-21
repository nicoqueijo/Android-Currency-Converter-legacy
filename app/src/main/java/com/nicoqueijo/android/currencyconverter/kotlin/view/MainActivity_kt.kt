package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
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
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.MainActivityViewModel_kt

class MainActivity_kt : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel_kt

    internal lateinit var drawer: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navController: NavController
    private lateinit var navView: NavigationView
    private lateinit var lastUpdateLabel: TextView
    private lateinit var closeAppToast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_kt)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel_kt::class.java)
        initBannerAd()
        initViews()
        handleNavigation()
        initLastUpdateLabel()
    }

    private fun initBannerAd() {
        MobileAds.initialize(this, resources.getString(R.string.app_id))
        val adView: AdView = findViewById(R.id.banner_ad_kt)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    @SuppressLint("ShowToast")
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar_kt)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer_layout_kt)
        initListeners()
        drawer.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navView = findViewById(R.id.nav_view_menu_kt)
        lastUpdateLabel = findViewById(R.id.last_updated_label)
        closeAppToast = Toast.makeText(this, R.string.tap_to_close, Toast.LENGTH_SHORT)
    }

    private fun handleNavigation() {
        navController = findNavController(R.id.content_frame_kt)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            viewModel._activeFragment.postValue(destination.id)
            viewModel.fragmentBackstackEntries.add(destination.id)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            drawer.closeDrawer(GravityCompat.START)
            when (viewModel.activeFragment.value) {
                R.id.activeCurrenciesFragment_kt -> {
                    handleBackstack(menuItem.itemId, R.id.activeCurrenciesFragment_kt)
                }
                R.id.sourceCodeFragment_kt -> {
                    handleBackstack(menuItem.itemId, R.id.sourceCodeFragment_kt)
                }
                R.id.selectableCurrenciesFragment_kt -> {
                    if (menuItem.itemId == R.id.activeCurrenciesFragment_kt) {
                        true
                    } else {
                        navController.navigate(menuItem.itemId)
                        true
                    }
                }
                R.id.errorFragment_kt -> {
                    showNoInternetSnackbar()
                    false
                }
                R.id.loadingCurrenciesFragment_kt -> false
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
                Utils.hideKeyboard(this@MainActivity_kt)
            }
        }
    }

    private fun initLastUpdateLabel() {
        viewModel.activeFragment.observe(this, Observer { activeFragment ->
            when (activeFragment) {
                R.id.activeCurrenciesFragment_kt ->
                    lastUpdateLabel.text = getString(R.string.last_update,
                            viewModel.getFormattedLastUpdate())
            }
        })
    }

    private fun showNoInternetSnackbar() {
        Snackbar.make(findViewById(R.id.content_frame_kt), R.string.no_internet, Snackbar.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
            // Document magic number or store in named variable
        } else if (navController.backStack.size > 2) {
            viewModel.fragmentBackstackEntries.remove(navController.currentDestination?.id!!)
            navController.popBackStack()
        } else if (!closeAppToast.view.isShown) {
            closeAppToast.show()
        } else {
            super.onBackPressed()
        }
    }
}
