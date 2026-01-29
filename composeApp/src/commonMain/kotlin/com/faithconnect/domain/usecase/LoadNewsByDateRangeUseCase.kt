package com.faithconnect.domain.usecase

import com.faithconnect.domain.model.NewsItem
import com.faithconnect.domain.repository.NewsItemRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Use case for loading news items within a specific date range.
 *
 * Business logic: Filters news items by publish date within the specified range.
 */
@OptIn(ExperimentalTime::class)
class LoadNewsByDateRangeUseCase(
    private val repository: NewsItemRepository
) {
    // Expose repository state (observe, don't copy!)
    val newsItems: StateFlow<List<NewsItem>> = repository.newsItems
    val isLoading: StateFlow<Boolean> = repository.isLoading
    val error: StateFlow<String?> = repository.error

    /**
     * Get news items within a date range.
     *
     * @param startDate Start of the date range (inclusive).
     * @param endDate End of the date range (inclusive).
     * @return Flow of news items within the specified date range, sorted by date (newest first).
     */
    operator fun invoke(startDate: Instant, endDate: Instant) =
        repository.newsItems.map { allNews ->
            allNews
                .filter { news ->
                    news.publishDate >= startDate && news.publishDate <= endDate
                }
                .sortedByDescending { it.publishDate }
        }

    /**
     * Get news items from the last N days.
     *
     * @param days Number of days to look back from today.
     * @return Flow of news items from the last N days.
     */
    fun getNewsFromLastDays(days: Int) = repository.newsItems.map { allNews ->
        val now = kotlin.time.Clock.System.now()
        val cutoffDate = now - kotlin.time.Duration.parse("${days}d")

        allNews
            .filter { news -> news.publishDate >= cutoffDate }
            .sortedByDescending { it.publishDate }
    }

    /**
     * Get news items by category within a date range.
     *
     * @param startDate Start of the date range (inclusive).
     * @param endDate End of the date range (inclusive).
     * @param category Category to filter by.
     * @return Flow of filtered news items.
     */
    fun getNewsByDateRangeAndCategory(
        startDate: Instant,
        endDate: Instant,
        category: String
    ) = repository.newsItems.map { allNews ->
        allNews
            .filter { news ->
                news.publishDate >= startDate &&
                news.publishDate <= endDate &&
                news.category.equals(category, ignoreCase = true)
            }
            .sortedByDescending { it.publishDate }
    }
}
