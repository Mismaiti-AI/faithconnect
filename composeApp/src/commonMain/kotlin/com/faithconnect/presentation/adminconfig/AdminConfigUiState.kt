package com.faithconnect.presentation.adminconfig

/**
 * UI State for Admin Configuration screen.
 *
 * Represents different states: Loading, Success (with data), Error.
 */
sealed interface AdminConfigUiState {
    /**
     * Initial loading state.
     */
    data object Loading : AdminConfigUiState

    /**
     * Success state with configuration data.
     *
     * @param sheetUrl Currently configured Google Sheets URL.
     * @param isValidUrl Whether the URL is valid format.
     * @param isTestingConnection Whether connection test is in progress.
     * @param connectionTestResult Result of connection test (null if not tested).
     * @param isSaving Whether configuration is being saved.
     * @param error Error message if any operation failed.
     */
    data class Success(
        val sheetUrl: String = "",
        val isValidUrl: Boolean = false,
        val isTestingConnection: Boolean = false,
        val connectionTestResult: ConnectionTestResult? = null,
        val isSaving: Boolean = false,
        val error: String? = null
    ) : AdminConfigUiState

    /**
     * Error state when configuration cannot be loaded.
     *
     * @param message Error message to display.
     */
    data class Error(val message: String) : AdminConfigUiState
}

/**
 * Result of connection test.
 */
sealed interface ConnectionTestResult {
    data object Success : ConnectionTestResult
    data class Failure(val message: String) : ConnectionTestResult
}
