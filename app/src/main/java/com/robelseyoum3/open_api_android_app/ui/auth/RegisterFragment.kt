package com.robelseyoum3.open_api_android_app.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.robelseyoum3.open_api_android_app.R
import com.robelseyoum3.open_api_android_app.util.ApiEmptyResponse
import com.robelseyoum3.open_api_android_app.util.ApiErrorResponse
import com.robelseyoum3.open_api_android_app.util.ApiSuccessResponse


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
        subscribeObservers()
    }

    private fun subscribeObservers() {

    }


}
