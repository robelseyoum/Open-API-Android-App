package com.robelseyoum3.open_api_android_app.api.auth

import androidx.lifecycle.LiveData
import com.robelseyoum3.open_api_android_app.api.auth.network_responses.LoginResponse
import com.robelseyoum3.open_api_android_app.api.auth.network_responses.RegistrationResponse
import com.robelseyoum3.open_api_android_app.util.GenericApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OpenApiAuthService {

    //"https://open-api.xyz/api/"
    //https://open-api.xyz/api/account/register

    @POST("account/login")
    @FormUrlEncoded
    fun login(
        @Field("username") email: String,
        @Field("password") password: String
    ) : LiveData<GenericApiResponse<LoginResponse>>

    @POST("account/register")
    @FormUrlEncoded
    fun register(
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("password2") password2: String
    ): LiveData<GenericApiResponse<RegistrationResponse>>

}