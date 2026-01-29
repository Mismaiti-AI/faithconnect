package com.faithconnect.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.faithconnect.data.local.dao.ChurchProfileDao
import com.faithconnect.data.local.dao.EventDao
import com.faithconnect.data.local.dao.NewsItemDao
import com.faithconnect.data.local.entity.ChurchProfileEntity
import com.faithconnect.data.local.entity.EventEntity
import com.faithconnect.data.local.entity.NewsItemEntity

/**
 * FaithConnect Room Database
 *
 * Contains three tables:
 * - events: Church events, seminars, workshops
 * - news_items: Church announcements and updates
 * - church_profile: Church information and contact details
 *
 * This database provides offline caching for data fetched from Google Sheets.
 */
@Database(
    entities = [
        EventEntity::class,
        NewsItemEntity::class,
        ChurchProfileEntity::class
    ],
    version = 1,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun newsItemDao(): NewsItemDao
    abstract fun churchProfileDao(): ChurchProfileDao
}
