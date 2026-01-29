package com.faithconnect.presentation.churchprofile

import com.faithconnect.domain.model.ChurchProfile

/**
 * UI State for Church Profile screen.
 *
 * Represents different states: Loading, Success (with profile data), Error.
 */
sealed interface ChurchProfileUiState {
    /**
     * Initial loading state.
     */
    data object Loading : ChurchProfileUiState

    /**
     * Success state with church profile data.
     *
     * @param profile The church profile data.
     * @param isRefreshing Whether a refresh is in progress.
     * @param error Error message if any operation failed.
     */
    data class Success(
        val profile: ChurchProfile? = null,
        val isRefreshing: Boolean = false,
        val error: String? = null
    ) : ChurchProfileUiState

    /**
     * Error state when profile cannot be loaded.
     *
     * @param message Error message to display.
     */
    data class Error(val message: String) : ChurchProfileUiState
}
