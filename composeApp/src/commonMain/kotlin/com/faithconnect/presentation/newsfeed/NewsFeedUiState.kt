package com.faithconnect.presentation.newsfeed

import com.faithconnect.domain.model.NewsItem
import kotlin.time.Instant

/**
 * UI State for News Feed screen.
 *
 * Represents different states: Loading, Success (with news items), Error.
 */
sealed interface NewsFeedUiState {
    /**
     * Initial loading state.
     */
    data object Loading : NewsFeedUiState

    /**
     * Success state with news feed data.
     *
     * @param newsItems List of news items.
     * @param isRefreshing Whether a refresh is in progress.
     * @param dateRangeStart Start of date filter range (null = no filter).
     * @param dateRangeEnd End of date filter range (null = no filter).
     * @param error Error message if any operation failed.
     */
    data class Success(
        val newsItems: List<NewsItem> = emptyList(),
        val isRefreshing: Boolean = false,
        val dateRangeStart: Instant? = null,
        val dateRangeEnd: Instant? = null,
        val error: String? = null
    ) : NewsFeedUiState

    /**
     * Error state when news items cannot be loaded.
     *
     * @param message Error message to display.
     */
    data class Error(val message: String) : NewsFeedUiState
}
