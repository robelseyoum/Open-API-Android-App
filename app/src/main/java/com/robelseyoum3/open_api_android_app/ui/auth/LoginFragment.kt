package com.robelseyoum3.open_api_android_app.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

import com.robelseyoum3.open_api_android_app.R
import com.robelseyoum3.open_api_android_app.model.AuthToken
import com.robelseyoum3.open_api_android_app.ui.auth.state.AuthStateEvent
import com.robelseyoum3.open_api_android_app.ui.auth.state.AuthStateEvent.*
import com.robelseyoum3.open_api_android_app.ui.auth.state.LoginFields
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "LoginFragment: ${viewModel.hashCode()}")

        login_button.setOnClickListener {
//            viewModel.setAuthToken(AuthToken(1, "gigisfrere"))
            login()
        }

        subscribeObservers()

    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { it ->
            it.loginField?.let { loginFields ->
                loginFields.login_email?.let { email -> input_email.setText(email) }
                loginFields.login_password?.let { password -> input_password.setText(password) }
            }
        })
    }

    fun login() {
        viewModel.setStateEvent(
            LoginAttemptEvent (
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setLoginField(
            LoginFields(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }

}
