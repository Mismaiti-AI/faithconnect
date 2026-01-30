package com.faithconnect.di

import org.koin.core.module.Module

/**
 * Platform-specific module.
 *
 * Each platform (Android, iOS) provides its own implementation
 * for platform-specific dependencies:
 * - AppDatabase (Room database builder)
 * - PreferencesDataSource (DataStore for Android, NSUserDefaults for iOS)
 *
 * This expect function allows each platform to register its own
 * dependencies without polluting common code.
 */
expect fun platformModule(): Module
