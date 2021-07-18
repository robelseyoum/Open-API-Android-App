package com.robelseyoum3.open_api_android_app.di.auth

import androidx.lifecycle.ViewModel
import com.robelseyoum3.open_api_android_app.di.ViewModelKey
import com.robelseyoum3.open_api_android_app.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel

}