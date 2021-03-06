package com.robelseyoum3.open_api_android_app.di.auth

import android.content.SharedPreferences
import com.robelseyoum3.open_api_android_app.api.auth.OpenApiAuthService
import com.robelseyoum3.open_api_android_app.persistence.AccountPropertiesDao
import com.robelseyoum3.open_api_android_app.persistence.AuthTokenDao
import com.robelseyoum3.open_api_android_app.repository.auth.AuthRepository
import com.robelseyoum3.open_api_android_app.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit


@Module
class AuthModule{

    @AuthScope
    @Provides
    fun provideOpenApiAuthService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService,
        sessionManager: SessionManager,
        sharedPreferences: SharedPreferences,
        sharedPrefsEditor: SharedPreferences.Editor
    ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager,
            sharedPreferences,
            sharedPrefsEditor
        )
    }

}