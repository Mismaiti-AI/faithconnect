package com.faithconnect.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "faithconnect_prefs")

/**
 * Android implementation using DataStore.
 */
actual class PreferencesDataSource(private val context: Context) {

    private val SHEET_URL_KEY = stringPreferencesKey("sheet_url")

    actual suspend fun setSheetUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[SHEET_URL_KEY] = url
        }
    }

    actual suspend fun getSheetUrl(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[SHEET_URL_KEY]
        }.first()
    }

    actual fun observeSheetUrl(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[SHEET_URL_KEY]
        }
    }

    actual suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
