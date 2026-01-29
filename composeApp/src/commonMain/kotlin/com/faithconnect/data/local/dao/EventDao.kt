package com.faithconnect.data.local.dao

import androidx.room.*
import com.faithconnect.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY date ASC")
    fun observeAll(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events ORDER BY date ASC")
    suspend fun getAll(): List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getById(id: String): EventEntity?

    @Query("SELECT * FROM events WHERE id = :id")
    fun observeById(id: String): Flow<EventEntity?>

    @Query("SELECT * FROM events WHERE category = :category ORDER BY date ASC")
    fun getByCategory(category: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE is_featured = 1 ORDER BY date ASC")
    fun getFeaturedEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE date >= :currentTimeMillis ORDER BY date ASC LIMIT :limit")
    suspend fun getUpcomingEvents(currentTimeMillis: Long, limit: Int = 10): List<EventEntity>

    @Upsert
    suspend fun insert(event: EventEntity)

    @Upsert
    suspend fun insertAll(events: List<EventEntity>)

    @Update
    suspend fun update(event: EventEntity)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM events")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(events: List<EventEntity>) {
        deleteAll()
        insertAll(events)
    }
}
