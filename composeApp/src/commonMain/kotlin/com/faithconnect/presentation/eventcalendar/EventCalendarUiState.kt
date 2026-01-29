package com.faithconnect.presentation.eventcalendar

import com.faithconnect.domain.model.Event

/**
 * UI State for Event Calendar screen.
 *
 * Represents different states: Loading, Success (with events), Error.
 */
sealed interface EventCalendarUiState {
    /**
     * Initial loading state.
     */
    data object Loading : EventCalendarUiState

    /**
     * Success state with events data.
     *
     * @param events List of upcoming events.
     * @param selectedCategory Currently selected category filter (null = all).
     * @param availableCategories List of all available event categories.
     * @param isRefreshing Whether a refresh is in progress.
     * @param error Error message if any operation failed.
     */
    data class Success(
        val events: List<Event> = emptyList(),
        val selectedCategory: String? = null,
        val availableCategories: List<String> = emptyList(),
        val isRefreshing: Boolean = false,
        val error: String? = null
    ) : EventCalendarUiState

    /**
     * Error state when events cannot be loaded.
     *
     * @param message Error message to display.
     */
    data class Error(val message: String) : EventCalendarUiState
}
