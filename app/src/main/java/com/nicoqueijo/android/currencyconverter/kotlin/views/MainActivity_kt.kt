package com.nicoqueijo.android.currencyconverter.kotlin.views

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.database.CurrencyDatabase
import com.nicoqueijo.android.currencyconverter.kotlin.services.RetrofitFactory
import kotlinx.android.synthetic.main.activity_main_kt.*
import kotlinx.coroutines.*
import java.util.*


const val TAG = "MeinActiviti"

class MainActivity_kt : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_kt)

//        CoroutineScope(Dispatchers.IO).launch {
//            Log.d(TAG, "${Thread.currentThread()}")
//            val response = RetrofitFactory
//                    .getRetrofitService()
//                    .getExchangeRates(getApiKey())
//
//            withContext(Dispatchers.Main) {
//                Log.d(TAG, "${Thread.currentThread()}")
//                if (response.isSuccessful) {
//                    view_body.text = response
//                            .body()
//                            ?.exchangeRates
//                            .toString()
//                } else {
//                    view_body.text = getString(R.string.error_description)
//                }
//            }
//        }

//        CoroutineScope(Dispatchers.IO).launch {
//            Log.d(TAG, "${Thread.currentThread()}")
//            val response = CurrencyDatabase
//                    .getInstance(applicationContext)
//                    .currencyDao()
//                    .getAllCurrencies()
//                    .toString()
//
//            withContext(Dispatchers.Main) {
//                Log.d(TAG, "${Thread.currentThread()}")
//                view_body.text = response
//            }
//        }

//        CoroutineScope(Dispatchers.Main).launch {
//            Log.d(TAG, "${Thread.currentThread()}")
//            mockNetworkCall()
//            view_body.text = "HELLO WORLD"
//        }

//        CoroutineScope(Dispatchers.IO).launch {
//            Log.d(TAG, "Operation: db calls. Thread: ${Thread.currentThread()}")
//            val currencyDatabase: CurrencyDatabase = CurrencyDatabase.getInstance(applicationContext)
//            val dao = currencyDatabase.currencyDao()
//            val currencies = dao.getAllCurrencies()
//            CoroutineScope(Dispatchers.Main).launch {
//                Log.d(TAG, "Operation: UI updated. Thread: ${Thread.currentThread()}")
//                view_body.text = currencies.toString()
//            }
//        }

//        CoroutineScope(Dispatchers.Main).launch {
//            println("Operation: call to getExchangeRatesFromServer(). Thread: ${Thread.currentThread()}")
//            getExchangeRatesFromServer()
//        }

    }

    private suspend fun mockNetworkCall() {
        runBlocking {
            Log.d(TAG, "${Thread.currentThread()}")
            delay(2000)
        }
    }

    private suspend fun getExchangeRatesFromServer() = withContext(Dispatchers.IO) {
        Log.d(TAG, "Operation: db calls. Thread: ${Thread.currentThread()}")
        val response = RetrofitFactory.getRetrofitService().getExchangeRates(getApiKey())
    }

    private fun getApiKey(): String {
        val apiKeys = resources.getStringArray(R.array.api_keys)
        val random = Random().nextInt(apiKeys.size)
        return apiKeys[random]
    }
}
