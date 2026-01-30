package com.faithconnect.data.repository

import com.faithconnect.data.local.preferences.PreferencesDataSource
import com.faithconnect.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of PreferencesRepository using platform-specific data source.
 */
class PreferencesRepositoryImpl(
    private val preferencesDataSource: PreferencesDataSource
) : PreferencesRepository {

    override suspend fun setSheetUrl(url: String) {
        preferencesDataSource.setSheetUrl(url)
    }

    override suspend fun getSheetUrl(): String? {
        return preferencesDataSource.getSheetUrl()
    }

    override fun observeSheetUrl(): Flow<String?> {
        return preferencesDataSource.observeSheetUrl()
    }

    override suspend fun clear() {
        preferencesDataSource.clear()
    }
}
