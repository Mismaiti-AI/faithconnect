package com.faithconnect.presentation.onboarding

/**
 * UI State for Onboarding screen.
 *
 * Represents different states during the onboarding process.
 */
sealed interface OnboardingUiState {
    /**
     * Initial loading state (checking if already configured).
     */
    data object Loading : OnboardingUiState

    /**
     * Ready for user input state.
     *
     * @param sheetUrl Current Google Sheets URL being entered.
     * @param isValidUrl Whether the URL format is valid.
     * @param isTestingConnection Whether connection test is in progress.
     * @param connectionTestResult Result of connection test (null if not tested).
     * @param isSaving Whether the configuration is being saved.
     * @param isSuccess Whether setup completed successfully.
     * @param error Error message if any operation failed.
     */
    data class Ready(
        val sheetUrl: String = "",
        val isValidUrl: Boolean = false,
        val isTestingConnection: Boolean = false,
        val connectionTestResult: ConnectionTestResult? = null,
        val isSaving: Boolean = false,
        val isSuccess: Boolean = false,
        val error: String? = null
    ) : OnboardingUiState

    /**
     * Error state when setup cannot proceed.
     *
     * @param message Error message to display.
     */
    data class Error(val message: String) : OnboardingUiState
}

/**
 * Result of connection test.
 */
sealed interface ConnectionTestResult {
    data object Success : ConnectionTestResult
    data class Failure(val message: String) : ConnectionTestResult
}
