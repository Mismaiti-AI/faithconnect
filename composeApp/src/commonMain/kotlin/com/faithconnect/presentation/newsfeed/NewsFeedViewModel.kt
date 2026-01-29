package com.faithconnect.presentation.newsfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithconnect.domain.usecase.GetLatestNewsUseCase
import com.faithconnect.domain.usecase.LoadNewsByDateRangeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Instant

/**
 * THIN ViewModel for News Feed screen.
 *
 * Observes repository state via use cases and delegates business logic to use cases.
 */
class NewsFeedViewModel(
    private val getLatestNewsUseCase: GetLatestNewsUseCase,
    private val loadNewsByDateRangeUseCase: LoadNewsByDateRangeUseCase
) : ViewModel() {

    private val _dateRangeStart = MutableStateFlow<Instant?>(null)
    private val _dateRangeEnd = MutableStateFlow<Instant?>(null)

    // Observe repository state via use case (don't copy!)
    val uiState: StateFlow<NewsFeedUiState> = combine(
        getLatestNewsUseCase.newsItems,
        getLatestNewsUseCase.isLoading,
        getLatestNewsUseCase.error,
        _dateRangeStart,
        _dateRangeEnd
    ) { newsItems, isLoading, error, dateStart, dateEnd ->
        when {
            error != null -> NewsFeedUiState.Error(error)
            newsItems.isNotEmpty() || !isLoading -> NewsFeedUiState.Success(
                newsItems = newsItems,
                isRefreshing = isLoading,
                dateRangeStart = dateStart,
                dateRangeEnd = dateEnd,
                error = null
            )
            else -> NewsFeedUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NewsFeedUiState.Loading
    )

    init {
        loadNews()
    }

    /**
     * Load latest news items from repository.
     */
    fun loadNews() {
        viewModelScope.launch {
            getLatestNewsUseCase()
        }
    }

    /**
     * Refresh news items from remote source.
     */
    fun refresh() {
        viewModelScope.launch {
            getLatestNewsUseCase.refresh()
        }
    }

    /**
     * Filter news by date range.
     *
     * @param startDate Start of date range.
     * @param endDate End of date range.
     */
    fun filterByDateRange(startDate: Instant, endDate: Instant) {
        viewModelScope.launch {
            _dateRangeStart.value = startDate
            _dateRangeEnd.value = endDate
            loadNewsByDateRangeUseCase(startDate, endDate)
        }
    }

    /**
     * Clear date range filter and show all news.
     */
    fun clearDateFilter() {
        _dateRangeStart.value = null
        _dateRangeEnd.value = null
        loadNews()
    }
}
