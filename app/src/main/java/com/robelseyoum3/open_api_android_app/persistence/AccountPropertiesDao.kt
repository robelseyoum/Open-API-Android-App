package com.robelseyoum3.open_api_android_app.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.robelseyoum3.open_api_android_app.model.AccountProperties

@Dao
interface AccountPropertiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAndReplace(accountProperties: AccountProperties): Long


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOnIgnore(accountProperties: AccountProperties)

    @Query("SELECT * FROM account_properties WhERE pk = :pk")
    fun searchByPk(pk: Int) : AccountProperties

    @Query("SELECT * FROM account_properties WhERE email = :email")
    fun searchByEmail(email: String) : AccountProperties?

}