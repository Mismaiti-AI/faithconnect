package com.faithconnect.presentation.newsdetail

import com.faithconnect.domain.model.NewsItem

/**
 * UI State for News Detail screen.
 *
 * Represents different states: Loading, Success (with news item details), Error.
 */
sealed interface NewsDetailUiState {
    /**
     * Initial loading state.
     */
    data object Loading : NewsDetailUiState

    /**
     * Success state with news item detail data.
     *
     * @param newsItem The news item details.
     * @param isRefreshing Whether a refresh is in progress.
     * @param error Error message if any operation failed.
     */
    data class Success(
        val newsItem: NewsItem? = null,
        val isRefreshing: Boolean = false,
        val error: String? = null
    ) : NewsDetailUiState

    /**
     * Error state when news item cannot be loaded.
     *
     * @param message Error message to display.
     */
    data class Error(val message: String) : NewsDetailUiState
}
