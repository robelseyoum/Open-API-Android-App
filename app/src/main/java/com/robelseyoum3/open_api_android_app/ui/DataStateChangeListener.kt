package com.robelseyoum3.open_api_android_app.ui

interface DataStateChangeListener {

    fun onDataStateChange(dataState: DataState<*>?)  //<*>  any UI related datastate

    fun expandAppbar()

    fun hideSoftKeyboard()
}