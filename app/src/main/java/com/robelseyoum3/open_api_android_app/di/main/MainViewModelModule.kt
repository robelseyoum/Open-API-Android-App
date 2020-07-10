package com.robelseyoum3.open_api_android_app.di.main

import androidx.lifecycle.ViewModel
import com.robelseyoum3.open_api_android_app.di.ViewModelKey
import com.robelseyoum3.open_api_android_app.ui.main.account.AccountViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel ::class)
    abstract fun bindAccountViewModel(accountViewModel: AccountViewModel): ViewModel
}