package com.faithconnect.data.repository

import com.faithconnect.core.network.ApiResult
import com.faithconnect.data.local.dao.NewsItemDao
import com.faithconnect.data.local.entity.toDomain
import com.faithconnect.data.local.entity.toEntity
import com.faithconnect.data.remote.GoogleSheetsService
import com.faithconnect.domain.model.NewsItem
import com.faithconnect.domain.repository.NewsItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Implementation of NewsItemRepository using Google Sheets as remote source
 * and Room database for offline caching.
 */
class NewsItemRepositoryImpl(
    private val googleSheetsService: GoogleSheetsService,
    private val newsItemDao: NewsItemDao,
    private val sheetUrlProvider: () -> String
) : NewsItemRepository {

    // Private mutable state
    private val _newsItems = MutableStateFlow<List<NewsItem>>(emptyList())
    private val _selectedNewsItem = MutableStateFlow<NewsItem?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    // Public immutable state
    override val newsItems: StateFlow<List<NewsItem>> = _newsItems.asStateFlow()
    override val selectedNewsItem: StateFlow<NewsItem?> = _selectedNewsItem.asStateFlow()
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    override val error: StateFlow<String?> = _error.asStateFlow()

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        repositoryScope.launch {
            newsItemDao.observeAll().collect { entities ->
                _newsItems.value = entities.map { it.toDomain() }
            }
        }
    }

    override suspend fun loadNewsItems(): ApiResult<Unit> {
        if (_newsItems.value.isNotEmpty()) {
            return ApiResult.Success(Unit)
        }
        return refreshNewsItems()
    }

    override suspend fun refreshNewsItems(): ApiResult<Unit> {
        _isLoading.value = true
        _error.value = null

        return try {
            val sheetUrl = sheetUrlProvider()
            if (sheetUrl.isBlank()) {
                _error.value = "Google Sheets URL not configured"
                _isLoading.value = false
                return ApiResult.Error(Exception("Sheet URL not configured"))
            }

            val newsItems = googleSheetsService.fetchNewsItems(sheetUrl)
            newsItemDao.replaceAll(newsItems.map { it.toEntity() })

            _isLoading.value = false
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false

            val cached = newsItemDao.getAll()
            if (cached.isNotEmpty()) {
                _newsItems.value = cached.map { it.toDomain() }
                _error.value = "Using cached data. ${e.message}"
                ApiResult.Success(Unit)
            } else {
                _error.value = e.message ?: "Failed to load news"
                ApiResult.Error(e)
            }
        }
    }

    override suspend fun selectNewsItem(newsId: String) {
        val newsItem = _newsItems.value.find { it.id == newsId }
        if (newsItem != null) {
            _selectedNewsItem.value = newsItem
            return
        }

        val cached = newsItemDao.getById(newsId)
        if (cached != null) {
            _selectedNewsItem.value = cached.toDomain()
        } else {
            _error.value = "News item not found: $newsId"
        }
    }

    override fun clearSelection() {
        _selectedNewsItem.value = null
    }

    override suspend fun getNewsItemById(id: String): ApiResult<NewsItem> {
        val cached = _newsItems.value.find { it.id == id }
        if (cached != null) {
            return ApiResult.Success(cached)
        }

        val local = newsItemDao.getById(id)
        if (local != null) {
            return ApiResult.Success(local.toDomain())
        }

        return ApiResult.Error(Exception("News item not found: $id"))
    }

    override fun clearError() {
        _error.value = null
    }
}
