package com.robelseyoum3.open_api_android_app.session

import android.app.Application
import com.robelseyoum3.open_api_android_app.persistence.AuthTokenDao

class SessionManager constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {
}