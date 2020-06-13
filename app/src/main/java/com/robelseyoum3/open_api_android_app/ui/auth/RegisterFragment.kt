package com.robelseyoum3.open_api_android_app.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.robelseyoum3.open_api_android_app.R
import com.robelseyoum3.open_api_android_app.ui.auth.state.AuthStateEvent
import com.robelseyoum3.open_api_android_app.ui.auth.state.RegistrationFields
import com.robelseyoum3.open_api_android_app.util.ApiEmptyResponse
import com.robelseyoum3.open_api_android_app.util.ApiErrorResponse
import com.robelseyoum3.open_api_android_app.util.ApiSuccessResponse
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "RegisterFragment:  ${viewModel.hashCode()} ")

        register_button.setOnClickListener {
            register()
        }

        subscribeObservers()
    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.registrationFields?.let { registerFields ->
                registerFields.registration_email?.let {register -> input_email.setText(register) }
                registerFields.registration_username?.let {register -> input_username.setText(register) }
                registerFields.registration_password?.let {register -> input_password.setText(register) }
                registerFields.registration_confirm_password?.let {register -> input_password_confirm.setText(register) }
            }
        })

    }

    fun register(){
        viewModel.setStateEvent(
            AuthStateEvent.RegisterAttemptEvent(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }

    //here when we leave this fragment and come back it will retain the value from textbox
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setRegistrationFields(
            RegistrationFields(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }
}
