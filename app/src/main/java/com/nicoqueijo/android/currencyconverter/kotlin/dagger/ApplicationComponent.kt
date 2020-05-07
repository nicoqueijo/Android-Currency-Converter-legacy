package com.nicoqueijo.android.currencyconverter.kotlin.dagger

import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.MainActivityViewModel
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.SelectorViewModel
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.SplashViewModel
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.WatchlistViewModel
import dagger.Component

@ApplicationScope
@Component(modules = [NetworkModule::class, DatabaseModule::class, SharedPreferencesModule::class, ContextModule::class])
interface ApplicationComponent {

    fun inject(viewModel: MainActivityViewModel)
    fun inject(viewModel: SplashViewModel)
    fun inject(viewModel: WatchlistViewModel)
    fun inject(viewModel: SelectorViewModel)
}