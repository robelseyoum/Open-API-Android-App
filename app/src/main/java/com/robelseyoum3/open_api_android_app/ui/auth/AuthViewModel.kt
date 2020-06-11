package com.robelseyoum3.open_api_android_app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.robelseyoum3.open_api_android_app.api.auth.network_responses.LoginResponse
import com.robelseyoum3.open_api_android_app.api.auth.network_responses.RegistrationResponse
import com.robelseyoum3.open_api_android_app.repository.auth.AuthRepository
import com.robelseyoum3.open_api_android_app.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    val authRepository: AuthRepository
) : ViewModel(){

    fun testLogin(): LiveData<GenericApiResponse<LoginResponse>> {
        return authRepository.testingLoginRequest(
            "robelseyoum3@gmail.com",
            "Zewdema78"
        )
    }


    fun testRegister(): LiveData<GenericApiResponse<RegistrationResponse>> {
        return authRepository.testRegistrationRequest(
            "robelseyoum3@gmail.com",
            "robalzm3",
            "Zewdema78",
            "Zewdema78"
        )
    }

}