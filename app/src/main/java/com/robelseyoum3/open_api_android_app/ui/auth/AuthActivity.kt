package com.robelseyoum3.open_api_android_app.ui.auth

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.robelseyoum3.open_api_android_app.R
import com.robelseyoum3.open_api_android_app.ui.BaseActivity
import com.robelseyoum3.open_api_android_app.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class AuthActivity : BaseActivity() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)

    }

}
