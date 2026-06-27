package com.digitalbank.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface IfscDao {
    @Query("SELECT * FROM ifsc_cache WHERE ifsc = :ifsc")
    suspend fun getIfsc(ifsc: String): IfscEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ifscEntity: IfscEntity)
}
