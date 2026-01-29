package com.faithconnect.presentation.eventcategoryfilter

/**
 * UI State for Event Category Filter screen.
 *
 * Represents different states: Loading, Success (with categories), Error.
 */
sealed interface EventCategoryFilterUiState {
    /**
     * Initial loading state.
     */
    data object Loading : EventCategoryFilterUiState

    /**
     * Success state with category filter data.
     *
     * @param availableCategories List of all available categories.
     * @param selectedCategories Set of currently selected categories.
     * @param isSaving Whether preferences are being saved.
     * @param error Error message if any operation failed.
     */
    data class Success(
        val availableCategories: List<String> = emptyList(),
        val selectedCategories: Set<String> = emptySet(),
        val isSaving: Boolean = false,
        val error: String? = null
    ) : EventCategoryFilterUiState

    /**
     * Error state when categories cannot be loaded.
     *
     * @param message Error message to display.
     */
    data class Error(val message: String) : EventCategoryFilterUiState
}
