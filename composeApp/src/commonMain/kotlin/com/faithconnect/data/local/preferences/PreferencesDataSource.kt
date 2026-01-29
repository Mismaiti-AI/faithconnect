package com.faithconnect.data.local.preferences

import kotlinx.coroutines.flow.Flow

/**
 * Platform-specific preferences data source.
 *
 * Implementations:
 * - Android: Uses DataStore
 * - iOS: Uses NSUserDefaults
 */
expect class PreferencesDataSource {
    suspend fun setSheetUrl(url: String)
    suspend fun getSheetUrl(): String?
    fun observeSheetUrl(): Flow<String?>
    suspend fun clear()
}
