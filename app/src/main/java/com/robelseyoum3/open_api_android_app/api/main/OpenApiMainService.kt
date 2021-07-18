package com.robelseyoum3.open_api_android_app.api.main

import androidx.lifecycle.LiveData
import com.robelseyoum3.open_api_android_app.api.GenericResponse
import com.robelseyoum3.open_api_android_app.model.AccountProperties
import com.robelseyoum3.open_api_android_app.util.GenericApiResponse
import retrofit2.http.*

interface OpenApiMainService {

    @GET("account/properties")
    fun getAccountProperties(
        @Header("Authorization") authorization: String
    ): LiveData<GenericApiResponse<AccountProperties>>


    @PUT("account/properties/update")
    @FormUrlEncoded
    fun saveAccountProperties(
        @Header("Authorization") authorization: String,
        @Field("email") email: String,
        @Field("username") username: String
    ): LiveData<GenericApiResponse<GenericResponse>>


    @PUT("account/change_password/")
    @FormUrlEncoded
    fun updatePassword(
        @Header("Authorization") authorization: String,
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_new_password") confirmNewPassword: String
    ): LiveData<GenericApiResponse<GenericResponse>>

}