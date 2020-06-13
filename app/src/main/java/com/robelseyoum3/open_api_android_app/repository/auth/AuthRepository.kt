package com.robelseyoum3.open_api_android_app.repository.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.robelseyoum3.open_api_android_app.api.auth.OpenApiAuthService
import com.robelseyoum3.open_api_android_app.api.auth.network_responses.LoginResponse
import com.robelseyoum3.open_api_android_app.api.auth.network_responses.RegistrationResponse
import com.robelseyoum3.open_api_android_app.model.AuthToken
import com.robelseyoum3.open_api_android_app.persistence.AccountPropertiesDao
import com.robelseyoum3.open_api_android_app.persistence.AuthTokenDao
import com.robelseyoum3.open_api_android_app.session.SessionManager
import com.robelseyoum3.open_api_android_app.ui.DataState
import com.robelseyoum3.open_api_android_app.ui.Response
import com.robelseyoum3.open_api_android_app.ui.ResponseType
import com.robelseyoum3.open_api_android_app.ui.auth.state.AuthViewState
import com.robelseyoum3.open_api_android_app.util.ApiEmptyResponse
import com.robelseyoum3.open_api_android_app.util.ApiErrorResponse
import com.robelseyoum3.open_api_android_app.util.ApiSuccessResponse
import com.robelseyoum3.open_api_android_app.util.GenericApiResponse
import com.robelseyoum3.open_api_android_app.util.HandlingErrors.Companion.ERROR_UNKNOWN

class AuthRepository constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager) {


    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>>{
        return openApiAuthService.login(email, password)
            .switchMap { switchResponse ->
                object : LiveData<DataState<AuthViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        when(switchResponse){
                            is ApiSuccessResponse -> {
                                value = DataState.data(
                                    data = (
                                            AuthViewState(
                                                authToken = AuthToken(
                                                    switchResponse.body.pk,
                                                    switchResponse.body.token
                                                )
                                            )
                                            ),
                                    response = null
                                )
                            }

                            is ApiErrorResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = switchResponse.errorMessage,
                                        responseType = ResponseType.Dialog
                                    )
                                )
                            }

                            is ApiEmptyResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = ERROR_UNKNOWN,
                                        responseType = ResponseType.Dialog
                                    )
                                )
                            }
                        }
                    }
                }
            }
    }



    fun attemptRegistration(
            email: String,
            username: String,
            password: String,
            confirmPassword: String
        ): LiveData<DataState<AuthViewState>>{
        return openApiAuthService.register(email, username, password, confirmPassword)
            .switchMap { switchResponse ->
                object : LiveData<DataState<AuthViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        when(switchResponse){
                            is ApiSuccessResponse -> {
                                value = DataState.data(
                                    data = (
                                            AuthViewState(
                                                authToken = AuthToken(
                                                    switchResponse.body.pk,
                                                    switchResponse.body.token
                                                )
                                            )
                                            ),
                                    response = null
                                )
                            }

                            is ApiErrorResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = switchResponse.errorMessage,
                                        responseType = ResponseType.Dialog
                                    )
                                )
                            }

                            is ApiEmptyResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = ERROR_UNKNOWN,
                                        responseType = ResponseType.Dialog
                                    )
                                )
                            }
                        }
                    }
                }
            }
    }
}
//    fun testingLoginRequest(email: String, password: String): LiveData<GenericApiResponse<LoginResponse>> {
//        return openApiAuthService.login(email, password)
//    }
//
//    fun testRegistrationRequest(
//        email: String,
//        username: String,
//        password: String,
//        confirmPassword: String
//    ): LiveData<GenericApiResponse<RegistrationResponse>> {
//        return openApiAuthService.register(email, username, password, confirmPassword)
//    }

