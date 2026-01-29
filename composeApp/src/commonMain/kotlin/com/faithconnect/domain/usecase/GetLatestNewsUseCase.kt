package com.faithconnect.domain.usecase

import com.faithconnect.core.network.ApiResult
import com.faithconnect.domain.model.NewsItem
import com.faithconnect.domain.repository.NewsItemRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

/**
 * Use case for retrieving latest news items.
 *
 * Business logic: Retrieves news items sorted by publish date (newest first),
 * with optional limit on number of items.
 */
class GetLatestNewsUseCase(
    private val repository: NewsItemRepository
) {
    // Expose repository state (observe, don't copy!)
    val newsItems: StateFlow<List<NewsItem>> = repository.newsItems
    val isLoading: StateFlow<Boolean> = repository.isLoading
    val error: StateFlow<String?> = repository.error

    /**
     * Load latest news items from repository.
     */
    suspend operator fun invoke(): ApiResult<Unit> {
        return repository.loadNewsItems()
    }

    /**
     * Refresh news items from remote source.
     */
    suspend fun refresh(): ApiResult<Unit> {
        return repository.refreshNewsItems()
    }

    /**
     * Get latest news items sorted by publish date (newest first).
     *
     * @param limit Maximum number of items to return. Pass null for all items.
     * @param urgentFirst If true, urgent items appear first before sorting by date.
     * @return Flow of latest news items.
     */
    fun getLatestNews(limit: Int? = null, urgentFirst: Boolean = false) =
        repository.newsItems.map { allNews ->
            var news = allNews

            // Sort by urgency first if requested
            if (urgentFirst) {
                news = news.sortedWith(
                    compareByDescending<NewsItem> { it.isUrgent }
                        .thenByDescending { it.publishDate }
                )
            } else {
                news = news.sortedByDescending { it.publishDate }
            }

            // Apply limit if specified
            if (limit != null && limit > 0) {
                news.take(limit)
            } else {
                news
            }
        }

    /**
     * Get urgent news items only.
     *
     * @return Flow of urgent news items sorted by publish date (newest first).
     */
    fun getUrgentNews() = repository.newsItems.map { allNews ->
        allNews
            .filter { it.isUrgent }
            .sortedByDescending { it.publishDate }
    }
}
