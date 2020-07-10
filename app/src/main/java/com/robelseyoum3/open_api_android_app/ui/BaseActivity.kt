package com.robelseyoum3.open_api_android_app.ui

import android.content.Context
import android.hardware.input.InputManager
import android.util.Log
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import com.robelseyoum3.open_api_android_app.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(), DataStateChangeListener {

    val TAG: String = "AppDebug"

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            GlobalScope.launch(Main){

                //for loading
                displayProgressBar(it.loading.isLoading)

                //check error case
                it.error?.let { errorEvent ->
                    handleStateError(errorEvent)
                }

                //for success data
                it.data?.let {
                    it.response?.let { responseEvent ->
                        handleStateResponse(responseEvent)
                    }
                }

            }
        }
    }

    private fun handleStateResponse(responseEvent: Event<Response>) {
        responseEvent.getContentIfNotHandled()?.let {
            when(it.responseType){
                is ResponseType.Toast -> {
                    it.message?.let { message ->
                        displayToast(message)
                    }
                }

                is ResponseType.Dialog -> {
                    it.message?.let { message ->
                        displaySuccessDialog(message)
                    }
                }

                is ResponseType.None -> {
                    Log.d(TAG, "handleStateResponse: ${it.message}")
                }
            }
        }
    }

    private fun handleStateError(errorEvent: Event<StateError>) {
        errorEvent.getContentIfNotHandled()?.let {

            when(it.response.responseType) {

                is ResponseType.Toast -> {
                    it.response.message?.let { message ->
                        displayToast(message)
                    }
                }

                is ResponseType.Dialog -> {
                    it.response.message?.let { message ->
                        displayErrorDialog(message)
                    }
                }

                is ResponseType.None -> {
                    Log.e(TAG, "handleStateError: ${it.response.message}")
                }
            }
        }
    }
    //used to hide the keyboard
    override fun hideSoftKeyboard() {
        if(currentFocus != null){
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            //here we tell to hide for any keyboard pop up
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    abstract fun displayProgressBar(boolean: Boolean)
}