package com.robelseyoum3.open_api_android_app.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GenericResponse (

    @SerializedName(value = "response")
    @Expose
    var response: String

)