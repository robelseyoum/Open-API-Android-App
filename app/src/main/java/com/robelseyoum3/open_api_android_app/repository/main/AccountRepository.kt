package com.robelseyoum3.open_api_android_app.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.robelseyoum3.open_api_android_app.api.GenericResponse
import com.robelseyoum3.open_api_android_app.api.main.OpenApiMainService
import com.robelseyoum3.open_api_android_app.model.AccountProperties
import com.robelseyoum3.open_api_android_app.model.AuthToken
import com.robelseyoum3.open_api_android_app.persistence.AccountPropertiesDao
import com.robelseyoum3.open_api_android_app.repository.JobManager
import com.robelseyoum3.open_api_android_app.repository.NetworkBoundResource
import com.robelseyoum3.open_api_android_app.session.SessionManager
import com.robelseyoum3.open_api_android_app.ui.DataState
import com.robelseyoum3.open_api_android_app.ui.Response
import com.robelseyoum3.open_api_android_app.ui.ResponseType
import com.robelseyoum3.open_api_android_app.ui.main.account.state.AccountViewState
import com.robelseyoum3.open_api_android_app.util.AbsentLiveData
import com.robelseyoum3.open_api_android_app.util.GenericApiResponse
import com.robelseyoum3.open_api_android_app.util.GenericApiResponse.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepository @Inject constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
) : JobManager("AccountRepository") {
    private val TAG: String = "AppDebug"

    fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>>{
        return  object: NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
        true
        ){

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<AccountProperties>) {
                updateLocalDb(response.body)
//                withContext(Main){
//                    //finish by viewing the cache
//                    result.addSource(loadFromCache()){accountViewState ->
//                        onCompleteJob(
//                            DataState.data(
//                                data = accountViewState,
//                                response = null
//                            )
//                        )
//                    }
//                }
                //or optionally it is also possible to return
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return openApiMainService
                    .getAccountProperties("Token ${authToken.token}")
            }

            override fun setJob(job: Job) {
                addJob("getAccountProperties", job)
            }

            //this request is used whenever the network is down and in transaction view the cache
            override suspend fun createCacheRequestAndReturn() {
                withContext(Main){
                    //finish by viewing the cache
                    result.addSource(loadFromCache()){accountViewState ->
                        onCompleteJob(
                            DataState.data(
                                data = accountViewState,
                                response = null
                            )
                        )
                    }
                }
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return accountPropertiesDao.searchByPk(authToken.account_pk!!)
                        //switchMap used to switch from AccountProperties into AccountViewState
                    .switchMap {
                        object : LiveData<AccountViewState>(){
                            override fun onActive() {
                                super.onActive()
                                value = AccountViewState(it)
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: AccountProperties?) {
                cacheObject?.let {
                    accountPropertiesDao.updateAccountProperties(
                        cacheObject.pk,
                        cacheObject.email,
                        cacheObject.username
                    )
                }
            }

        }.asLiveData()
    }

    fun saveAccountProperties(authToken: AuthToken, accountProperties: AccountProperties)
            : LiveData<DataState<AccountViewState>>
    {
        return object : NetworkBoundResource<GenericResponse,Any, AccountViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                //todo
                updateLocalDb(null)
                withContext(Main){
                    //finish with success response
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(response.body.response, ResponseType.Toast)
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.saveAccountProperties(
                    "Token ${authToken.token}",
                    accountProperties.email,
                    accountProperties.username
                )
            }

            override fun setJob(job: Job) {
                addJob("saveAccountProperties", job)
            }

            //Not applicable we are not looking from cache
            override suspend fun createCacheRequestAndReturn() {
            }

            //not used in this case
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {
                return accountPropertiesDao.updateAccountProperties(
                    accountProperties.pk,
                    accountProperties.email,
                    accountProperties.username
                )
            }

        }.asLiveData()
    }

    fun updatePassword(
        authToken: AuthToken,
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): LiveData<DataState<AccountViewState>>{
        return object : NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                withContext(Main){
                    //finish with success response
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(response.body.response, ResponseType.Toast)
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return  openApiMainService.updatePassword(
                    "Token ${authToken.token!!}",
                    currentPassword,
                    newPassword,
                    confirmNewPassword
                )
            }

            override fun setJob(job: Job) {
                addJob("updatePassword", job)
            }

            override suspend fun createCacheRequestAndReturn() {
            }
            //not applicable
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }
            //not applicable
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }

        }.asLiveData()
    }
}