package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.*
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import kotlinx.android.synthetic.main.activity_main_kt.*
import kotlinx.coroutines.*

const val TAG = "MeinActiviti"

class MainActivity_kt : AppCompatActivity() {

    private var interstitialAd: InterstitialAd? = null
    internal lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var closeAppToast: Toast

    private lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_kt)

        repository = Repository(this)
        var currencies: List<Currency>

        CoroutineScope(Dispatchers.Main).launch {
            currencies = withContext(Dispatchers.Main) {
                repository.getAllCurrencies()
            }
            val something = 9
        }

//        initAds()
//        initViews()
//
//        navController = findNavController(R.id.content_frame_kt)
//        nav_view_menu_kt.setupWithNavController(navController)
    }

    private fun initAds() {
        initBannerAd()
        initInterstitialAd()
    }

    private fun initBannerAd() {
        MobileAds.initialize(this, resources.getString(R.string.app_id))
        val adView = findViewById<AdView>(R.id.banner_ad_kt)
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
        setSupportActionBar(toolbar_kt)
        drawerLayout = findViewById(R.id.drawer_layout_kt)
        initListeners()
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        closeAppToast = Toast.makeText(this, R.string.tap_to_close, Toast.LENGTH_SHORT)
    }

    private fun initListeners() {
        actionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, toolbar_kt,
                R.string.nav_drawer_open, R.string.nav_drawer_close) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                hideKeyboard()
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    // Restore this method when I have the navigation/backstack figured out
//    override fun onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START)
////        } else if (mFragmentManager.getBackStackEntryCount() > 0) {
////            mFragmentManager.popBackStack()
//        } else if (!closeAppToast.view.isShown) {
//            closeAppToast.show()
//        } else {
//            super.onBackPressed()
//        }
//    }

    companion object {

        val currencies = listOf(
                Currency("USD_AED", 3.6731),
                Currency("USD_AFN", 76.400002),
                Currency("USD_ALL", 110.05),
                Currency("USD_AMD", 478.627644),
                Currency("USD_ANG", 1.644796),
                Currency("USD_AOA", 495.798),
                Currency("USD_ARS", 60.268339),
                Currency("USD_AUD", 1.494191),
                Currency("USD_AWG", 1.8),
                Currency("USD_AZN", 1.7025),
                Currency("USD_BAM", 1.77177),
                Currency("USD_BBD", 2.0),
                Currency("USD_BDT", 84.810816),
                Currency("USD_BGN", 1.763403),
                Currency("USD_BHD", 0.376995),
                Currency("USD_BIF", 1891.0),
                Currency("USD_BMD", 1.0),
                Currency("USD_BND", 1.365409),
                Currency("USD_BOB", 6.914286),
                Currency("USD_BRL", 4.2825),
                Currency("USD_BSD", 1.0),
                Currency("USD_BTN", 71.27808),
                Currency("USD_BWP", 10.916025),
                Currency("USD_BYN", 2.140531),
                Currency("USD_BZD", 2.0155),
                Currency("USD_CAD", 1.32376),
                Currency("USD_CDF", 1685.25),
                Currency("USD_CHF", 0.963471),
                Currency("USD_CLP", 800.298522),
                Currency("USD_CNY", 6.9367),
                Currency("USD_COP", 3420.07),
                Currency("USD_CRC", 565.939932),
                Currency("USD_CUP", 25.75),
                Currency("USD_CVE", 100.15),
                Currency("USD_CZK", 22.73345),
                Currency("USD_DJF", 178.0),
                Currency("USD_DKK", 6.73485),
                Currency("USD_DOP", 53.46),
                Currency("USD_DZD", 120.04663),
                Currency("USD_EGP", 15.800006),
                Currency("USD_ERN", 14.999786),
                Currency("USD_ETB", 32.1),
                Currency("USD_EUR", 0.901364),
                Currency("USD_FJD", 2.195),
                Currency("USD_FKP", 0.757312),
                Currency("USD_GBP", 0.757312),
                Currency("USD_GEL", 2.89),
                Currency("USD_GGP", 0.757312),
                Currency("USD_GHS", 5.48),
                Currency("USD_GIP", 0.757312),
                Currency("USD_GMD", 51.1),
                Currency("USD_GNF", 9412.5),
                Currency("USD_GTQ", 7.66421),
                Currency("USD_GYD", 208.534643),
                Currency("USD_HKD", 7.76626),
                Currency("USD_HNL", 24.82),
                Currency("USD_HRK", 6.710021),
                Currency("USD_HTG", 99.363589),
                Currency("USD_HUF", 303.96),
                Currency("USD_IDR", 13637.634),
                Currency("USD_ILS", 3.4485),
                Currency("USD_IMP", 0.757312),
                Currency("USD_INR", 71.499),
                Currency("USD_IQD", 1190.0),
                Currency("USD_IRR", 42140.76018),
                Currency("USD_ISK", 123.00999),
                Currency("USD_JEP", 0.757312),
                Currency("USD_JMD", 139.529549),
                Currency("USD_JOD", 0.7085),
                Currency("USD_JPY", 108.394976),
                Currency("USD_KES", 100.35),
                Currency("USD_KGS", 69.670193),
                Currency("USD_KHR", 4060.0),
                Currency("USD_KMF", 443.74972),
                Currency("USD_KPW", 900.0),
                Currency("USD_KRW", 1195.97),
                Currency("USD_KWD", 0.303864),
                Currency("USD_KYD", 0.833269),
                Currency("USD_KZT", 379.46905),
                Currency("USD_LAK", 8885.0),
                Currency("USD_LBP", 1514.0),
                Currency("USD_LKR", 181.430245),
                Currency("USD_LRD", 195.0),
                Currency("USD_LSL", 15.01),
                Currency("USD_LYD", 1.405),
                Currency("USD_MAD", 9.59625),
                Currency("USD_MDL", 17.314613),
                Currency("USD_MGA", 3625.0),
                Currency("USD_MKD", 55.741144),
                Currency("USD_MMK", 1462.844399),
                Currency("USD_MNT", 2707.687708),
                Currency("USD_MOP", 8.000217),
                Currency("USD_MRO", 357.0),
                Currency("USD_MUR", 36.781609),
                Currency("USD_MVR", 15.46),
                Currency("USD_MWK", 735.0),
                Currency("USD_MXN", 18.8436),
                Currency("USD_MYR", 4.0961),
                Currency("USD_MZN", 63.749989),
                Currency("USD_NAD", 15.01),
                Currency("USD_NGN", 361.5),
                Currency("USD_NIO", 34.15),
                Currency("USD_NOK", 9.2058),
                Currency("USD_NPR", 114.048649),
                Currency("USD_NZD", 1.547126),
                Currency("USD_OMR", 0.384993),
                Currency("USD_PAB", 1.0),
                Currency("USD_PEN", 3.385),
                Currency("USD_PGK", 3.38),
                Currency("USD_PHP", 51.008177),
                Currency("USD_PKR", 154.45),
                Currency("USD_PLN", 3.8729),
                Currency("USD_PYG", 6533.8452),
                Currency("USD_QAR", 3.641),
                Currency("USD_RON", 4.3079),
                Currency("USD_RSD", 106.15),
                Currency("USD_RUB", 63.975),
                Currency("USD_RWF", 935.0),
                Currency("USD_SAR", 3.751678),
                Currency("USD_SBD", 8.267992),
                Currency("USD_SCR", 13.53),
                Currency("USD_SDG", 48.95),
                Currency("USD_SEK", 9.631715),
                Currency("USD_SGD", 1.364875),
                Currency("USD_SHP", 0.757312),
                Currency("USD_SLL", 7484.709937),
                Currency("USD_SOS", 586.0),
                Currency("USD_SRD", 7.458),
                Currency("USD_SSP", 130.26),
                Currency("USD_STN", 22.325),
                Currency("USD_SVC", 8.748511),
                Currency("USD_SYP", 515.00441),
                Currency("USD_SZL", 15.01),
                Currency("USD_THB", 31.215713),
                Currency("USD_TJS", 9.693582),
                Currency("USD_TMT", 3.505),
                Currency("USD_TND", 2.815),
                Currency("USD_TOP", 2.316522),
                Currency("USD_TRY", 5.982),
                Currency("USD_TTD", 6.75593),
                Currency("USD_TWD", 30.361),
                Currency("USD_TZS", 2308.7),
                Currency("USD_UAH", 25.048804),
                Currency("USD_UGX", 3676.593754)
        )

        suspend fun mockExpensiveCall() {
            delay(500)
        }
    }
}
