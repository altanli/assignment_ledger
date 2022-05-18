package com.jobget.ledger.database

import androidx.room.*
import com.jobget.ledger.model.TransItem
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    /**
     * CREATE
     */
    //insert data to room database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertToRoomDatabase(transItem: TransItem) : Long

    /**
     * READ
     */
    //get all users inserted to room database...normally this is supposed to be a list of users
    @Transaction
    @Query("SELECT * FROM trans_table")
    fun getTransDetails() : Flow<List<TransItem>>

    //get single user inserted to room database
    @Transaction
    @Query("SELECT * FROM trans_table WHERE id = :id ORDER BY id DESC")
    fun getSingleUserDetails(id: Long) : Flow<TransItem>

    /**
     * UPDATE
     */
    //update user details
    @Update
    suspend fun updateTransDetails(transItem: TransItem)

    /**
     * DELETE
     */
    //delete single user details
    @Query("DELETE FROM trans_table WHERE id = :id")
    fun deleteSingleUserDetails(id: Int)

}