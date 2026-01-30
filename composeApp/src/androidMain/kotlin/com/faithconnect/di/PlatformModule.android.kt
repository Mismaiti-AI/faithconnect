package com.faithconnect.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.faithconnect.core.database.AppDatabase
import com.faithconnect.data.local.preferences.PreferencesDataSource
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific platform module.
 *
 * Provides:
 * - AppDatabase with Android Context
 * - PreferencesDataSource using DataStore
 */
actual fun platformModule() = module {
    // ========================================
    // ROOM DATABASE (Android)
    // ========================================
    single {
        val appContext = androidContext().applicationContext
        val dbFile = appContext.getDatabasePath("faithconnect.db")

        Room.databaseBuilder<AppDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .fallbackToDestructiveMigration(true)
            .build()
    }

    // ========================================
    // PREFERENCES DATA SOURCE (Android DataStore)
    // ========================================
    single {
        PreferencesDataSource(
            context = androidContext()
        )
    }
}
