package com.faithconnect.di

import androidx.room.RoomDatabase
import com.faithconnect.core.database.AppDatabase
import org.koin.dsl.module

/**
 * Database module for Room database and DAOs.
 *
 * Database instance is provided by platform-specific implementation
 * via expect/actual pattern (handled in platform modules).
 *
 * DAOs are derived from the database instance.
 */
val databaseModule = module {
    // ========================================
    // DATABASE INSTANCE
    // ========================================
    // Platform-specific database builder is registered in platformModule
    // This module provides DAOs derived from that database

    // ========================================
    // DAOs (Derived from Database)
    // ========================================

    single {
        get<AppDatabase>().eventDao()
    }

    single {
        get<AppDatabase>().newsItemDao()
    }

    single {
        get<AppDatabase>().churchProfileDao()
    }
}
