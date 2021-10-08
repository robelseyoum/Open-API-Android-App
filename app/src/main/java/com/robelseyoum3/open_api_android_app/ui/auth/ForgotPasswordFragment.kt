package com.robelseyoum3.open_api_android_app.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.robelseyoum3.open_api_android_app.R
import com.robelseyoum3.open_api_android_app.ui.DataState
import com.robelseyoum3.open_api_android_app.ui.DataStateChangeListener
import com.robelseyoum3.open_api_android_app.ui.Response
import com.robelseyoum3.open_api_android_app.ui.ResponseType
import com.robelseyoum3.open_api_android_app.util.Constants
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ClassCastException

/**
 * A simple [Fragment] subclass.
 */
class ForgotPasswordFragment : BaseAuthFragment() {

    lateinit var webView: WebView

    lateinit var stateChangeListener: DataStateChangeListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "ForgotPasswordFragment: ${viewModel.hashCode()} ")

        webView = view.findViewById(R.id.webview)

        loadPasswordWebView()

        return_to_launcher_fragment.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun loadPasswordWebView(){

        stateChangeListener.onDataStateChange(DataState.loading(isLoading = true, cachedData = null)) //show progressbar

        webView.webViewClient = object : WebViewClient() { //detect certain state of the webview

            override fun onPageFinished(view: WebView?, url: String?) { //when the webview comes on the view
                super.onPageFinished(view, url)

                stateChangeListener.onDataStateChange(
                    DataState.loading(isLoading = false, cachedData = null) //hide progressbar
                )
            }
        }

        webView.loadUrl(Constants.PASSWORD_RESET_URL)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(WebAppInterface(webInteractionCallback), "AndroidTextListener")
    }

    private val webInteractionCallback = object: WebAppInterface.OnWebInteractionCallback {

        override fun onSuccess(email: String) {
            Log.d(TAG, "onSuccess: a reset link will be sent to $email ")
            onPasswordResetLinkSent()
        }

        override fun onError(errorMessage: String) {
            Log.d(TAG, "onError: $errorMessage ")

            val dataState = DataState.error<Any>(
                response = Response(errorMessage, ResponseType.Dialog))
            stateChangeListener.onDataStateChange(dataState = dataState)
        }

        override fun onLoading(isLoading: Boolean) {
            Log.d(TAG, "onError: $isLoading ")
            GlobalScope.launch(Main){
                stateChangeListener.onDataStateChange(
                    DataState.loading(isLoading = isLoading, cachedData = null)
                )
            }
        }
    }

    private fun onPasswordResetLinkSent() {
        GlobalScope.launch(Main){
            parent_view.removeView(webView) //FrameLayout
            val animation = TranslateAnimation(
                password_reset_done_container.width.toFloat(),
                0f,
                0f,
                0f
            )
            animation.duration = 500
            password_reset_done_container.startAnimation(animation)
            password_reset_done_container.visibility = View.VISIBLE
        }
    }

    //Callback interface
    class WebAppInterface constructor(private val callback: OnWebInteractionCallback) {

        private val TAG: String = "AppDebug"

        //this javascript interface function is used to communicate with webView
        @JavascriptInterface
        fun onSuccess(email: String){
            callback.onSuccess(email)
        }

        @JavascriptInterface
        fun onError(errorMessage: String) {
            callback.onError(errorMessage)
        }

        @JavascriptInterface
        fun onLoading(isLoading: Boolean){
            callback.onLoading(isLoading)
        }

        interface OnWebInteractionCallback{
            fun onSuccess(email: String)

            fun onError(errorMessage: String)

            fun onLoading(isLoading: Boolean)
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
        stateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement DataStateChangeListener..")
        }

    }

}
