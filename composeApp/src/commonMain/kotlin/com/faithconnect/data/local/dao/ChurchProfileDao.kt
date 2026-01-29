package com.faithconnect.data.local.dao

import androidx.room.*
import com.faithconnect.data.local.entity.ChurchProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChurchProfileDao {
    @Query("SELECT * FROM church_profile WHERE id = 'default' LIMIT 1")
    fun observe(): Flow<ChurchProfileEntity?>

    @Query("SELECT * FROM church_profile WHERE id = 'default' LIMIT 1")
    suspend fun get(): ChurchProfileEntity?

    @Upsert
    suspend fun insert(profile: ChurchProfileEntity)

    @Update
    suspend fun update(profile: ChurchProfileEntity)

    @Query("DELETE FROM church_profile")
    suspend fun deleteAll()

    @Transaction
    suspend fun replace(profile: ChurchProfileEntity) {
        deleteAll()
        insert(profile)
    }
}
