package com.robelseyoum3.open_api_android_app.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Because of the Foreignkeys relations, AuthToken can't create new raw unless we have pk from
 * AccountProperties
 */
@Entity(
    tableName = "auth_token",
    foreignKeys = [
    ForeignKey(
        entity = AccountProperties::class,
        parentColumns = ["pk"],
        childColumns = ["account_pk"],
        onDelete = CASCADE
        )
    ]
)
data class AuthToken(

    @PrimaryKey
    @ColumnInfo(name = "account_pk")
    var account_pk: Int? = -1,

    @SerializedName("token")
    @Expose
    @ColumnInfo(name ="token")
    var token: String? = null

)