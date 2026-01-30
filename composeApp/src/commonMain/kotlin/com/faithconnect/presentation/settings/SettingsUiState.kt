package com.faithconnect.presentation.settings

/**
 * UI State for Settings screen.
 *
 * Represents different states: Loading, Success (with preferences), Error.
 */
sealed interface SettingsUiState {
    /**
     * Initial loading state.
     */
    data object Loading : SettingsUiState

    /**
     * Success state with settings data.
     *
     * @param sheetUrl Currently configured Google Sheets URL.
     * @param preferredCategories Set of preferred event/news categories.
     * @param isDarkMode Whether dark mode is enabled.
     * @param isSaving Whether preferences are being saved.
     * @param isClearingCache Whether cache is being cleared.
     * @param error Error message if any operation failed.
     */
    data class Success(
        val sheetUrl: String? = null,
        val preferredCategories: Set<String> = emptySet(),
        val isDarkMode: Boolean = false,
        val isSaving: Boolean = false,
        val isClearingCache: Boolean = false,
        val error: String? = null
    ) : SettingsUiState

    /**
     * Error state when settings cannot be loaded.
     *
     * @param message Error message to display.
     */
    data class Error(val message: String) : SettingsUiState
}
