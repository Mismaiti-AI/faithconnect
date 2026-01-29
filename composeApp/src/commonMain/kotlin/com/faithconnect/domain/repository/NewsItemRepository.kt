package com.faithconnect.domain.repository

import com.faithconnect.core.network.ApiResult
import com.faithconnect.domain.model.NewsItem
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for NewsItem data operations.
 *
 * Provides shared state management for news items across multiple screens.
 * All ViewModels observe the same StateFlow instances from this repository.
 */
interface NewsItemRepository {
    // Shared state - observable by all ViewModels
    val newsItems: StateFlow<List<NewsItem>>
    val selectedNewsItem: StateFlow<NewsItem?>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>

    // Actions - called by ViewModels/UseCases
    suspend fun loadNewsItems(): ApiResult<Unit>
    suspend fun refreshNewsItems(): ApiResult<Unit>
    suspend fun selectNewsItem(newsId: String)
    fun clearSelection()
    suspend fun getNewsItemById(id: String): ApiResult<NewsItem>
    fun clearError()
}
