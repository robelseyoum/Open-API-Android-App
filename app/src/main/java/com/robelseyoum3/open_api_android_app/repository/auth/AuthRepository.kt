package com.robelseyoum3.open_api_android_app.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.robelseyoum3.open_api_android_app.api.auth.OpenApiAuthService
import com.robelseyoum3.open_api_android_app.api.auth.network_responses.LoginResponse
import com.robelseyoum3.open_api_android_app.api.auth.network_responses.RegistrationResponse
import com.robelseyoum3.open_api_android_app.model.AccountProperties
import com.robelseyoum3.open_api_android_app.model.AuthToken
import com.robelseyoum3.open_api_android_app.persistence.AccountPropertiesDao
import com.robelseyoum3.open_api_android_app.persistence.AuthTokenDao
import com.robelseyoum3.open_api_android_app.repository.JobManager
import com.robelseyoum3.open_api_android_app.repository.NetworkBoundResource
import com.robelseyoum3.open_api_android_app.session.SessionManager
import com.robelseyoum3.open_api_android_app.ui.DataState
import com.robelseyoum3.open_api_android_app.ui.Response
import com.robelseyoum3.open_api_android_app.ui.ResponseType
import com.robelseyoum3.open_api_android_app.ui.auth.state.AuthViewState
import com.robelseyoum3.open_api_android_app.ui.auth.state.LoginFields
import com.robelseyoum3.open_api_android_app.ui.auth.state.RegistrationFields
import com.robelseyoum3.open_api_android_app.util.AbsentLiveData
import com.robelseyoum3.open_api_android_app.util.GenericApiResponse
import com.robelseyoum3.open_api_android_app.util.GenericApiResponse.*
import com.robelseyoum3.open_api_android_app.util.HandlingErrors.Companion.ERROR_SAVE_ACCOUNT_PROPERTIES
import com.robelseyoum3.open_api_android_app.util.HandlingErrors.Companion.ERROR_SAVE_AUTH_TOKEN
import com.robelseyoum3.open_api_android_app.util.HandlingErrors.Companion.ERROR_UNKNOWN
import com.robelseyoum3.open_api_android_app.util.HandlingErrors.Companion.GENERIC_AUTH_ERROR
import com.robelseyoum3.open_api_android_app.util.PreferenceKeys
import com.robelseyoum3.open_api_android_app.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository @Inject constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences, // this to read from the sharedpreference
    val sharedPrefsEditor: SharedPreferences.Editor // this is to write into shared preference
) : JobManager("AuthRepository") {

    private val TAG: String = "AppDebug"

    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>>{
        val loginFieldError = LoginFields(email, password).isValidForLogin()
        if(loginFieldError != LoginFields.LoginError.none()){
            return returnErrorResponse(loginFieldError, ResponseType.Dialog)
        }

        return object : NetworkBoundResource<LoginResponse, Any, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
        false
        ){
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")

                //Incorrect login credentials counts as a 200 response from server, so need to handle that
                if(response.body.response.equals(GENERIC_AUTH_ERROR)){
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                //don't care about result, Just insert if it doesn't exist b/c foreign key relationship
                accountPropertiesDao.insertOnIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )

                //will return -1 if failure
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )

                if(result < 0){
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog)
                        )
                    )
                }

                saveAuthenticatedUserToPrefs(email) //here we pass email to store it into sharedpreference

                onCompleteJob(
                    DataState.data(data = AuthViewState(authToken = AuthToken(response.body.pk, response.body.token)))
                )

            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            override fun setJob(job: Job) {
                addJob("attemptLogin", job)
            }
            //not used in this case
            override suspend fun createCacheRequestAndReturn() {
            }

            //not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            //not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {}

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

        return object : NetworkBoundResource<RegistrationResponse, Any, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
        false
        ){
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {

                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                if(response.body.response.equals(GENERIC_AUTH_ERROR)){
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                val result1 = accountPropertiesDao.insertAndReplace(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        response.body.username
                    )
                )

                // will return -1 if failure
                if(result1 < 0){
                    onCompleteJob(DataState.error(
                        Response(ERROR_SAVE_ACCOUNT_PROPERTIES, ResponseType.Dialog))
                    )
                    return
                }

                // will return -1 if failure
                val result2 = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )

                if(result2 < 0){
                    onCompleteJob(DataState.error(
                        Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog)
                    ))
                    return
                }

                saveAuthenticatedUserToPrefs(email) //this will share email to sharedpreference

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
                addJob("attemptRegistration", job)
            }

            //not used in this case
            override suspend fun createCacheRequestAndReturn() {}

            //not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            //not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }

        }.asLiveData()
    }

    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>>{
        val previousAuthUserEmail: String? =
            sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)
        if(previousAuthUserEmail.isNullOrBlank()) {
            Log.d(TAG, "checkPreviousAuthUser: No previously authenticated user found..")
            return returnNoTokenFound()
        } else {
            return object: NetworkBoundResource<Void, Any, AuthViewState>(
                sessionManager.isConnectedToTheInternet(),
                false,
                false,
                false
            ){
                //not used in this case
                override fun loadFromCache(): LiveData<AuthViewState> {
                    return AbsentLiveData.create()
                }

                //not used in this case
                override suspend fun updateLocalDb(cacheObject: Any?) {
                }

                //not used in this case
                override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Void>) {
                }
                //not used in this case
                override fun createCall(): LiveData<GenericApiResponse<Void>> {
                    return AbsentLiveData.create()
                }

                override fun setJob(job: Job) {
                    addJob("checkPreviousAuthUser", job)
                }

                override suspend fun createCacheRequestAndReturn() {
                    accountPropertiesDao.searchByEmail(previousAuthUserEmail).let { accountProperties ->
                        Log.d(TAG, "createCacheRequestAndReturn: searching for token: $accountProperties")

                        accountProperties?.let {
                            if(accountProperties.pk > -1) //primary key value started from zero(0)
                            {
                                authTokenDao.searchByPk(accountProperties.pk).let { authToken ->

                                        authToken?.let{
                                            authToken.token?.let{
                                            onCompleteJob(
                                                DataState.data(
                                                    data = AuthViewState(
                                                        authToken = authToken
                                                    )
                                                )
                                            )
                                            return
                                        }
                                    }
                                }
                            }
                        }

                        Log.d(TAG, "checkPreviousAuthUser: AuthToken not found")
                        onCompleteJob(
                            DataState.data(
                                data = null,
                                response = Response(
                                    RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                    ResponseType.None
                                )
                            )
                        )
                    }
                }

            }.asLiveData()
        }
    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    data = null,
                    response = Response(
                        RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE, ResponseType.None)
                )
            }
        }
    }

    //store the email into shared preference for pre authentication
    private fun saveAuthenticatedUserToPrefs(email: String) {
        sharedPrefsEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        sharedPrefsEditor.apply()
    }

    private fun returnErrorResponse(fieldsError: String, responseType: ResponseType):
            LiveData<DataState<AuthViewState>> {

        Log.d(TAG, "returnErrorResponse: $fieldsError")

        return object : LiveData<DataState<AuthViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    Response(
                        fieldsError,
                        responseType
                    )
                )
            }
        }
    }

    //here it will called from viewwmodel
//    fun cancelActiveJobs(){
//        Log.d(TAG, "AuthRepository: Cancelling on-going jobs..")
//        repositoryJob?.cancel()
//    }

    fun testingLoginRequest(email: String, password: String): LiveData<GenericApiResponse<LoginResponse>> {
        return openApiAuthService.login(email, password)
    }

    fun testRegistrationRequest(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<GenericApiResponse<RegistrationResponse>> {
        return openApiAuthService.register(email, username, password, confirmPassword)
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

