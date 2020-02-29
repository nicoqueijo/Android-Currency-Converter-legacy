package com.nicoqueijo.android.currencyconverter.kotlin.data

//        setupActionBarWithNavController(navController, drawer_layout_kt)

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