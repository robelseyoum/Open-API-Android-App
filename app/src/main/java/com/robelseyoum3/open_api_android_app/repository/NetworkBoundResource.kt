package com.robelseyoum3.open_api_android_app.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.robelseyoum3.open_api_android_app.ui.DataState
import com.robelseyoum3.open_api_android_app.ui.Response
import com.robelseyoum3.open_api_android_app.ui.ResponseType
import com.robelseyoum3.open_api_android_app.util.*
import com.robelseyoum3.open_api_android_app.util.Constants.Companion.NETWORK_TIMEOUT
import com.robelseyoum3.open_api_android_app.util.Constants.Companion.TESTING_NETWORK_DELAY
import com.robelseyoum3.open_api_android_app.util.GenericApiResponse.*
import com.robelseyoum3.open_api_android_app.util.HandlingErrors.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.robelseyoum3.open_api_android_app.util.HandlingErrors.Companion.ERROR_UNKNOWN
import com.robelseyoum3.open_api_android_app.util.HandlingErrors.Companion.UNABLE_TODO_OPERATION_WO_INTERNET
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

abstract class NetworkBoundResource<ResponseObject, CacheObject, ViewStateType>(
    isNetworkAvailable: Boolean, //is there a network connection?
    isNetworkRequest: Boolean, // is this a network request
    shouldCancelIfNoInternet: Boolean, //should this job cancelled if there is no network?
    shouldLoadFromCache: Boolean //should the cached data be loaded?
) {
    private val TAG: String = "AppDebug"

    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {

        setJob(initNewJob())
        setValue(DataState.loading(isLoading = true, cachedData = null))

        //here we add the store cache data into the database
        if(shouldLoadFromCache){
            val dbSource = loadFromCache()
            result.addSource(dbSource){
                result.removeSource(dbSource)
                setValue(DataState.loading(isLoading = true, cachedData = it))
            }
        }

        if(isNetworkRequest){
            if(isNetworkAvailable){
                doNetworkRequest()
            } else {
                if(shouldCancelIfNoInternet){
                    onErrorReturn(UNABLE_TODO_OPERATION_WO_INTERNET, shouldUseDialog = true, shouldUseToast = false)
                } else{
                    doCacheRequest()
                }
            }
        } else {
            doCacheRequest()
        }
    }

    private fun doCacheRequest() {
        coroutineScope.launch {
            //fake delay for testing cache
            delay(TESTING_NETWORK_DELAY)

            //view data from cache ONLY and return
            createCacheRequestAndReturn()
        }
    }

    private fun doNetworkRequest() {
        coroutineScope.launch {
            //simulate a network delay for testing
            delay(TESTING_NETWORK_DELAY)

            withContext(Main){

                //make network call
                val apiResponse = createCall()
                result.addSource(apiResponse){ response ->
                    result.removeSource(apiResponse)

                    coroutineScope.launch {
                        handleNetworkCall(response)
                    }
                }
            }
        }
        GlobalScope.launch(IO){
            delay(NETWORK_TIMEOUT)

            if(!job.isCompleted){
                Log.e(TAG, "NetworkBoundResource: JOB NETWORK TIMEOUT." )
                job.cancel(CancellationException(HandlingErrors.UNABLE_TO_RESOLVE_HOST))
            }
        }
    }

    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?){

        when(response){

            is ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }

            is ApiErrorResponse -> {
                Log.e(TAG, "NetworkBoundResource: ${response.errorMessage}")
                onErrorReturn(response.errorMessage, true, false)
            }

            is ApiEmptyResponse -> {
                Log.e(TAG, "NetworkBoundResource: Request returned NOTHING (HTTP 204")
                onErrorReturn("HTTP 204. Returned nothing.", true, false)
            }

        }
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>){
        GlobalScope.launch(Main){
            job.complete() //this will invoke the method invoke(from initNewJob) and will go to (else if) //Do nothing. Should be handled already

            setValue(dataState)
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>){
        result.value = dataState
    }

    fun onErrorReturn(errorMessage: String?, shouldUseDialog: Boolean, shouldUseToast: Boolean) {
        var msg = errorMessage
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None

        if(msg == null){
            msg = ERROR_UNKNOWN
        } else if(HandlingErrors.isNetworkError(msg))
        {
            msg = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if (shouldUseToast)
        {
            responseType = ResponseType.Toast
        }
        if (useDialog){
            responseType = ResponseType.Dialog
        }

        onCompleteJob(DataState.error(
            response = Response(
                message = msg,
                responseType = responseType
            )
        ))

    }

    @UseExperimental(InternalCoroutinesApi::class)
    private fun initNewJob(): Job {

        Log.d(TAG, "initNewJob: called....")

        job = Job()
        job.invokeOnCompletion(onCancelling = true, invokeImmediately = true, handler = object : CompletionHandler {

            override fun invoke(cause: Throwable?) {

                if(job.isCancelled){

                    Log.e(TAG, "NetworkBoundResource: Job has been cancelled.")
                    cause?.let {
                        onErrorReturn(it.message, false, true)
                    }?: onErrorReturn(ERROR_UNKNOWN, false, true)

                } else if(job.isCompleted) {
                    Log.e(TAG, "NetworkBoundResource: Job has been completed..")
                    //Do nothing. Should be handled already
                }
            }

        })

        coroutineScope = CoroutineScope(IO + job)

        return job
    }

    abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    abstract fun setJob(job: Job)

    abstract suspend fun createCacheRequestAndReturn()

    abstract fun loadFromCache(): LiveData<ViewStateType>

    abstract suspend fun updateLocalDb(cacheObject: CacheObject?)

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

}