package com.faithconnect.presentation.newsdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithconnect.domain.repository.NewsItemRepository
import com.faithconnect.domain.usecase.MarkNewsAsReadUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * THIN ViewModel for News Detail screen.
 *
 * Observes repository state and delegates business logic to use cases.
 * Requires a newsId parameter to load the specific news item.
 */
class NewsDetailViewModel(
    private val newsId: String,
    private val repository: NewsItemRepository,
    private val markNewsAsReadUseCase: MarkNewsAsReadUseCase
) : ViewModel() {

    // Observe repository state (don't copy!)
    val uiState: StateFlow<NewsDetailUiState> = combine(
        repository.selectedNewsItem,
        repository.isLoading,
        repository.error
    ) { selectedNewsItem, isLoading, error ->
        when {
            error != null -> NewsDetailUiState.Error(error)
            selectedNewsItem != null && selectedNewsItem.id == newsId -> NewsDetailUiState.Success(
                newsItem = selectedNewsItem,
                isRefreshing = isLoading,
                error = null
            )
            !isLoading && selectedNewsItem == null -> NewsDetailUiState.Error("News item not found")
            else -> NewsDetailUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NewsDetailUiState.Loading
    )

    init {
        loadNewsItem()
        markAsRead()
    }

    /**
     * Load news item details by ID.
     */
    private fun loadNewsItem() {
        viewModelScope.launch {
            repository.selectNewsItem(newsId)
        }
    }

    /**
     * Mark this news item as read.
     * Called automatically when the screen is viewed.
     */
    private fun markAsRead() {
        viewModelScope.launch {
            markNewsAsReadUseCase(newsId)
        }
    }

    /**
     * Clear the selected news item when leaving the screen.
     */
    override fun onCleared() {
        super.onCleared()
        repository.clearSelection()
    }
}
