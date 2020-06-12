package com.robelseyoum3.open_api_android_app.di

import com.robelseyoum3.open_api_android_app.di.auth.AuthFragmentBuildersModule
import com.robelseyoum3.open_api_android_app.di.auth.AuthModule
import com.robelseyoum3.open_api_android_app.di.auth.AuthScope
import com.robelseyoum3.open_api_android_app.di.auth.AuthViewModelModule
import com.robelseyoum3.open_api_android_app.ui.auth.AuthActivity
import com.robelseyoum3.open_api_android_app.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

}