package com.faithconnect.presentation.eventdetail

import com.faithconnect.domain.model.Event

/**
 * UI State for Event Detail screen.
 *
 * Represents different states: Loading, Success (with event details), Error.
 */
sealed interface EventDetailUiState {
    /**
     * Initial loading state.
     */
    data object Loading : EventDetailUiState

    /**
     * Success state with event detail data.
     *
     * @param event The event details.
     * @param isRefreshing Whether a refresh is in progress.
     * @param error Error message if any operation failed.
     */
    data class Success(
        val event: Event? = null,
        val isRefreshing: Boolean = false,
        val error: String? = null
    ) : EventDetailUiState

    /**
     * Error state when event cannot be loaded.
     *
     * @param message Error message to display.
     */
    data class Error(val message: String) : EventDetailUiState
}
