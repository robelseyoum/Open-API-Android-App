package com.robelseyoum3.open_api_android_app.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.robelseyoum3.open_api_android_app.model.AuthToken

@Dao
interface AuthTokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) //if data already exist it will replace it
    suspend fun insert(authToken: AuthToken): Long

    //for similar pk it will make it the token null
    @Query("UPDATE auth_token SET token = null WHERE account_pk = :pk")
    fun nullifyToken(pk: Int): Int

    @Query("SELECT * FROM auth_token WHERE account_pk =:pk")
    suspend fun searchByPk(pk: Int): AuthToken?

}