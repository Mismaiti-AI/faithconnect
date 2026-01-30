package com.faithconnect.core.database

import androidx.room.RoomDatabaseConstructor

/**
 * Platform-specific database builder
 * Room KSP will generate the actual implementations for each platform
 */
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
