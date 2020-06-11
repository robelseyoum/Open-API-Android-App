package com.robelseyoum3.open_api_android_app.di

import androidx.lifecycle.ViewModelProvider
import com.robelseyoum3.open_api_android_app.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}