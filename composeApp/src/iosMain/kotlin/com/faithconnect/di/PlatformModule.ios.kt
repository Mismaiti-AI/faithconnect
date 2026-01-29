package com.faithconnect.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.faithconnect.core.database.AppDatabase
import com.faithconnect.data.local.preferences.PreferencesDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/**
 * iOS-specific platform module.
 *
 * Provides:
 * - AppDatabase with iOS Documents directory path
 * - PreferencesDataSource using NSUserDefaults
 */
actual fun platformModule() = module {
    // ========================================
    // ROOM DATABASE (iOS)
    // ========================================
    single {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        val dbPath = requireNotNull(documentDirectory?.path) + "/faithconnect.db"

        Room.databaseBuilder<AppDatabase>(
            name = dbPath
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .fallbackToDestructiveMigration(true)
            .build()
    }

    // ========================================
    // PREFERENCES DATA SOURCE (iOS NSUserDefaults)
    // ========================================
    single {
        PreferencesDataSource()
    }
}
