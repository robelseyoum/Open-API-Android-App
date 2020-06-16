package com.robelseyoum3.open_api_android_app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.robelseyoum3.open_api_android_app.R
import com.robelseyoum3.open_api_android_app.ui.BaseActivity
import com.robelseyoum3.open_api_android_app.ui.auth.state.AuthStateEvent
import com.robelseyoum3.open_api_android_app.ui.main.MainActivity
import com.robelseyoum3.open_api_android_app.viewmodels.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_auth.progress_bar

import javax.inject.Inject

class AuthActivity : BaseActivity(),
    NavController.OnDestinationChangedListener
{

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)

        findNavController(R.id.auth_nav_host_fragment).addOnDestinationChangedListener(this)

        checkPreviousAuthUser()
        subscribeObservers()
    }

    private fun subscribeObservers() {

        viewModel.dataState.observe(this, Observer { dataState ->


            dataState.data?.let { data ->

                data.data?.let { event ->
                 event.getContentIfNotHandled()?.let {
                     it.authToken?.let {
                         Log.d(TAG, "AuthActivity, DataState: $it")
                         viewModel.setAuthToken(it)
                         }
                    }
                }
            }

            onDataStateChange(dataState)
        })

        viewModel.viewState.observe(this, Observer { it ->
            it.authToken?.let { authToken ->
                sessionManager.login(authToken) }
        })

        sessionManager.cachedToken.observe(this, Observer { authToken ->
            Log.d(TAG, "AuthActivity, subscribeObservers: ViewState: $authToken")

            if(authToken != null && authToken.account_pk != -1 && authToken.token != null)
            {
                navMainActivity()
            }
        })
    }

    private fun checkPreviousAuthUser(){
        viewModel.setStateEvent(AuthStateEvent.CheckPreviousAuthEvent)
    }
    private fun navMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        viewModel.cancelActiveJobs()
    }

    override fun displayProgressBar(boolean: Boolean) {
        if(boolean){
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }

}
