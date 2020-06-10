package com.robelseyoum3.open_api_android_app.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.robelseyoum3.open_api_android_app.model.AccountProperties
import com.robelseyoum3.open_api_android_app.model.AuthToken

@Database(entities = [AuthToken::class, AccountProperties::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun getAuthTokenDao(): AuthTokenDao

    abstract fun getAccountProperties(): AccountProperties

    companion object {
        const val DATABASE_NAME = "app_db"
    }

}