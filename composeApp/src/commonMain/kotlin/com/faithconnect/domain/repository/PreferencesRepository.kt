package com.faithconnect.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for storing app preferences.
 *
 * Used to persist configuration like the Google Sheets URL.
 */
interface PreferencesRepository {
    suspend fun setSheetUrl(url: String)
    suspend fun getSheetUrl(): String?
    fun observeSheetUrl(): Flow<String?>
    suspend fun clear()
}
