package com.robelseyoum3.open_api_android_app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.robelseyoum3.open_api_android_app.api.auth.network_responses.LoginResponse
import com.robelseyoum3.open_api_android_app.api.auth.network_responses.RegistrationResponse
import com.robelseyoum3.open_api_android_app.model.AuthToken
import com.robelseyoum3.open_api_android_app.repository.auth.AuthRepository
import com.robelseyoum3.open_api_android_app.ui.BaseViewModel
import com.robelseyoum3.open_api_android_app.ui.DataState
import com.robelseyoum3.open_api_android_app.ui.auth.state.AuthStateEvent
import com.robelseyoum3.open_api_android_app.ui.auth.state.AuthStateEvent.*
import com.robelseyoum3.open_api_android_app.ui.auth.state.AuthViewState
import com.robelseyoum3.open_api_android_app.ui.auth.state.LoginFields
import com.robelseyoum3.open_api_android_app.ui.auth.state.RegistrationFields
import com.robelseyoum3.open_api_android_app.util.AbsentLiveData
import com.robelseyoum3.open_api_android_app.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    val authRepository: AuthRepository
) : BaseViewModel<AuthStateEvent, AuthViewState>() {

    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        return when(stateEvent){

            is LoginAttemptEvent -> {
                AbsentLiveData.create()
            }

            is RegisterAttemptEvent -> {
                AbsentLiveData.create()
            }

            is CheckPreviousAuthEvent -> {
                AbsentLiveData.create()
            }
        }
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    fun setRegistrationFields(registrationFields: RegistrationFields){
        val update = getCurrentViewStateOrNew()
        if(update.registrationFields == registrationFields){
            return
        }
        update.registrationFields = registrationFields
        _viewState.value = update
    }

    fun setLoginField(loginFields: LoginFields){
        val update = getCurrentViewStateOrNew()
        if(update.loginField == loginFields){
            return
        }
        update.loginField = loginFields
        _viewState.value = update
    }

    fun setAuthToken(authToken: AuthToken){
        val update = getCurrentViewStateOrNew()
        if(update.authToken == authToken){
            return
        }
        update.authToken = authToken
        _viewState.value = update
    }

}