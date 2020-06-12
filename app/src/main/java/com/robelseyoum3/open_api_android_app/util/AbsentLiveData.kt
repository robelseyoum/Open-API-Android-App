package com.robelseyoum3.open_api_android_app.util

import androidx.lifecycle.LiveData

class AbsentLiveData <T : Any?> private constructor(): LiveData<T>(){


    init {
        postValue(null)
    }

    companion object {

        fun <T> create(): LiveData<T> {
            return  AbsentLiveData()
        }
    }
}