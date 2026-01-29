package com.faithconnect.domain.usecase

import com.faithconnect.domain.repository.PreferencesRepository

/**
 * Use case for setting the Google Sheets URL configuration.
 *
 * Business logic: Validates and persists the Google Sheets URL that will be used
 * to fetch church data (events, news, profile).
 */
class SetGoogleSheetUrlUseCase(
    private val preferencesRepository: PreferencesRepository
) {
    /**
     * Set the Google Sheets URL.
     *
     * Business logic: Validates the URL format before saving.
     *
     * @param url The Google Sheets URL (edit or published URL).
     * @return Result indicating success or failure with error message.
     */
    suspend operator fun invoke(url: String): Result<Unit> {
        // Validate URL format
        val validationError = validateUrl(url)
        if (validationError != null) {
            return Result.failure(IllegalArgumentException(validationError))
        }

        // Save to preferences
        return try {
            preferencesRepository.setSheetUrl(url)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Validate Google Sheets URL format.
     *
     * Business logic: Checks if URL is a valid Google Sheets URL.
     *
     * @param url URL to validate.
     * @return Error message if invalid, null if valid.
     */
    fun validateUrl(url: String): String? {
        if (url.isBlank()) {
            return "URL cannot be empty"
        }

        // Check if it's a Google Sheets URL
        if (!url.contains("docs.google.com/spreadsheets")) {
            return "Invalid Google Sheets URL. URL must contain 'docs.google.com/spreadsheets'"
        }

        // Check if it has a sheet ID
        val sheetIdRegex = """/spreadsheets/d/([a-zA-Z0-9-_]+)""".toRegex()
        if (!sheetIdRegex.containsMatchIn(url)) {
            return "Invalid Google Sheets URL format. Cannot extract sheet ID"
        }

        return null // Valid
    }

    /**
     * Get current Google Sheets URL.
     *
     * @return Currently configured URL, or null if not set.
     */
    suspend fun getCurrentUrl(): String? {
        return preferencesRepository.getSheetUrl()
    }

    /**
     * Check if URL is configured.
     *
     * @return True if a URL has been saved.
     */
    suspend fun isConfigured(): Boolean {
        val url = preferencesRepository.getSheetUrl()
        return !url.isNullOrBlank() && validateUrl(url) == null
    }

    /**
     * Clear the saved URL.
     */
    suspend fun clearUrl() {
        preferencesRepository.clear()
    }
}
