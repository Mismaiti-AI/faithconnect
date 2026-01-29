package com.faithconnect.data.local.preferences

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSUserDefaults

/**
 * iOS implementation using NSUserDefaults.
 */
actual class PreferencesDataSource {

    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val SHEET_URL_KEY = "sheet_url"

    // StateFlow for observing changes
    private val _sheetUrlFlow = MutableStateFlow<String?>(null)

    init {
        // Initialize flow with current value
        _sheetUrlFlow.value = userDefaults.stringForKey(SHEET_URL_KEY)
    }

    actual suspend fun setSheetUrl(url: String) {
        userDefaults.setObject(url, forKey = SHEET_URL_KEY)
        userDefaults.synchronize()
        _sheetUrlFlow.value = url
    }

    actual suspend fun getSheetUrl(): String? {
        return userDefaults.stringForKey(SHEET_URL_KEY)
    }

    actual fun observeSheetUrl(): Flow<String?> {
        return _sheetUrlFlow.asStateFlow()
    }

    actual suspend fun clear() {
        userDefaults.removeObjectForKey(SHEET_URL_KEY)
        userDefaults.synchronize()
        _sheetUrlFlow.value = null
    }
}
