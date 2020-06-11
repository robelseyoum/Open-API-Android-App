package com.robelseyoum3.open_api_android_app.session

import android.app.Application
import com.robelseyoum3.open_api_android_app.persistence.AuthTokenDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {
}