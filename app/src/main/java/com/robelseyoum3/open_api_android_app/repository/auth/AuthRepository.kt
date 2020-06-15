package com.robelseyoum3.open_api_android_app.repository.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.robelseyoum3.open_api_android_app.api.auth.OpenApiAuthService
import com.robelseyoum3.open_api_android_app.api.auth.network_responses.LoginResponse
import com.robelseyoum3.open_api_android_app.api.auth.network_responses.RegistrationResponse
import com.robelseyoum3.open_api_android_app.model.AuthToken
import com.robelseyoum3.open_api_android_app.persistence.AccountPropertiesDao
import com.robelseyoum3.open_api_android_app.persistence.AuthTokenDao
import com.robelseyoum3.open_api_android_app.repository.NetworkBoundResource
import com.robelseyoum3.open_api_android_app.session.SessionManager
import com.robelseyoum3.open_api_android_app.ui.DataState
import com.robelseyoum3.open_api_android_app.ui.Response
import com.robelseyoum3.open_api_android_app.ui.ResponseType
import com.robelseyoum3.open_api_android_app.ui.auth.state.AuthViewState
import com.robelseyoum3.open_api_android_app.ui.auth.state.LoginFields
import com.robelseyoum3.open_api_android_app.ui.auth.state.RegistrationFields
import com.robelseyoum3.open_api_android_app.util.ApiEmptyResponse
import com.robelseyoum3.open_api_android_app.util.ApiErrorResponse
import com.robelseyoum3.open_api_android_app.util.ApiSuccessResponse
import com.robelseyoum3.open_api_android_app.util.GenericApiResponse
import com.robelseyoum3.open_api_android_app.util.HandlingErrors.Companion.ERROR_UNKNOWN
import com.robelseyoum3.open_api_android_app.util.HandlingErrors.Companion.GENERIC_AUTH_ERROR
import kotlinx.coroutines.Job

class AuthRepository constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager) {

    private val TAG: String = "AppDebug"
    private var repositoryJob: Job? = null

    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>>{
        val loginFieldError = LoginFields(email, password).isValidForLogin()
        if(loginFieldError != LoginFields.LoginError.none()){
            return returnErrorResponse(loginFieldError, ResponseType.Dialog)
        }

        return object : NetworkBoundResource<LoginResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet()
        ){
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")
                if(response.body.response.equals(GENERIC_AUTH_ERROR)){
                    return onErrorReturn(response.body.errorMessage, true, false)
                }
                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )

            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()

    }


    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>>{
        val registrationFieldErrors = RegistrationFields(email,username, password, confirmPassword ).isValidForRegistration()
        if(registrationFieldErrors != RegistrationFields.RegistrationError.none())
        {
            return returnErrorResponse(registrationFieldErrors, ResponseType.Dialog)
        }

        return object : NetworkBoundResource<RegistrationResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet()
        ){
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")
                if(response.body.response.equals(GENERIC_AUTH_ERROR)){
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )

            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.register(email, username, password, confirmPassword)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()
    }

    private fun returnErrorResponse(fieldsError: String, responseType: ResponseType.Dialog): LiveData<DataState<AuthViewState>> {
        Log.d(TAG, "returnErrorResponse: $fieldsError")

        return object : LiveData<DataState<AuthViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    Response(
                        loginFieldError,
                        responseType
                    )
                )
            }
        }
    }

    //here it will called from viewwmodel
    fun cancelActiveJobs(){
        Log.d(TAG, "AuthRepository: Cancelling on-going jobs..")
        repositoryJob?.cancel()
    }
}





//    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>>{
//        return openApiAuthService.login(email, password)
//            .switchMap { switchResponse ->
//                object : LiveData<DataState<AuthViewState>>(){
//                    override fun onActive() {
//                        super.onActive()
//                        when(switchResponse){
//                            is ApiSuccessResponse -> {
//                                value = DataState.data(
//                                    data = (
//                                            AuthViewState(
//                                                authToken = AuthToken(
//                                                    switchResponse.body.pk,
//                                                    switchResponse.body.token
//                                                )
//                                            )
//                                            ),
//                                    response = null
//                                )
//                            }
//
//                            is ApiErrorResponse -> {
//                                value = DataState.error(
//                                    response = Response(
//                                        message = switchResponse.errorMessage,
//                                        responseType = ResponseType.Dialog
//                                    )
//                                )
//                            }
//
//                            is ApiEmptyResponse -> {
//                                value = DataState.error(
//                                    response = Response(
//                                        message = ERROR_UNKNOWN,
//                                        responseType = ResponseType.Dialog
//                                    )
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//    }
//
//
//
//    fun attemptRegistration(
//            email: String,
//            username: String,
//            password: String,
//            confirmPassword: String
//        ): LiveData<DataState<AuthViewState>>{
//        return openApiAuthService.register(email, username, password, confirmPassword)
//            .switchMap { switchResponse ->
//                object : LiveData<DataState<AuthViewState>>(){
//                    override fun onActive() {
//                        super.onActive()
//                        when(switchResponse){
//                            is ApiSuccessResponse -> {
//                                value = DataState.data(
//                                    data = (
//                                            AuthViewState(
//                                                authToken = AuthToken(
//                                                    switchResponse.body.pk,
//                                                    switchResponse.body.token
//                                                )
//                                            )
//                                            ),
//                                    response = null
//                                )
//                            }
//
//                            is ApiErrorResponse -> {
//                                value = DataState.error(
//                                    response = Response(
//                                        message = switchResponse.errorMessage,
//                                        responseType = ResponseType.Dialog
//                                    )
//                                )
//                            }
//
//                            is ApiEmptyResponse -> {
//                                value = DataState.error(
//                                    response = Response(
//                                        message = ERROR_UNKNOWN,
//                                        responseType = ResponseType.Dialog
//                                    )
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//    }
//}
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

