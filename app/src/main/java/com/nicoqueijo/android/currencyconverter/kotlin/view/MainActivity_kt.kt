package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.MainActivityViewModel
import kotlinx.coroutines.delay

class MainActivity_kt : AppCompatActivity() {

    internal lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navController: NavController
    private lateinit var navView: NavigationView
    private lateinit var closeAppToast: Toast
    private var interstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_kt)
        activityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        initAds()
        initViews()
        handleNavigation()
    }

    private fun handleNavigation() {
        navController = findNavController(R.id.content_frame_kt)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            activityViewModel._activeFragment.postValue(destination.id)
            activityViewModel.fragmentBackstackEntries.add(destination.id)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawer(GravityCompat.START)
            when (activityViewModel.activeFragment.value) {
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
            activityViewModel.fragmentBackstackEntries.contains(menuItemId) -> {
                navController.popBackStack(menuItemId, false)
                activityViewModel.fragmentBackstackEntries.remove(activeFragment)
                true
            }
            else -> {
                navController.navigate(menuItemId)
                activityViewModel.fragmentBackstackEntries.add(menuItemId)
                true
            }
        }
    }

    private fun initAds() {
        initBannerAd()
        initInterstitialAd()
    }

    private fun initBannerAd() {
        MobileAds.initialize(this, resources.getString(R.string.app_id))
        val adView: AdView = findViewById(R.id.banner_ad_kt)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun initInterstitialAd() {
        interstitialAd = InterstitialAd(this)
        interstitialAd!!.adUnitId = resources.getString(R.string.ad_unit_id_interstitial_test)
        interstitialAd!!.loadAd(AdRequest.Builder().build())
        interstitialAd!!.adListener = object : AdListener() {
            override fun onAdClosed() {
                interstitialAd = null
            }
        }
    }

    @SuppressLint("ShowToast")
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar_kt)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout_kt)
        initListeners()
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navView = findViewById(R.id.nav_view_menu_kt)
        closeAppToast = Toast.makeText(this, R.string.tap_to_close, Toast.LENGTH_SHORT)
    }

    private fun initListeners() {
        actionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close) {

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                hideKeyboard()
            }
        }
    }

    private fun showNoInternetSnackbar() {
        Snackbar.make(findViewById(R.id.content_frame_kt), R.string.no_internet, Snackbar.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            // Document magic number or store in named variable
        } else if (navController.backStack.size > 2) {
            activityViewModel.fragmentBackstackEntries.remove(navController.currentDestination?.id!!)
            navController.popBackStack()
        } else if (!closeAppToast.view.isShown) {
            closeAppToast.show()
        } else {
            super.onBackPressed()
        }
    }

    companion object {

        lateinit var activityViewModel: MainActivityViewModel

    }

}
