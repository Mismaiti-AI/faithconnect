package com.faithconnect.presentation.eventcalendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithconnect.domain.usecase.FilterEventsByCategoryUseCase
import com.faithconnect.domain.usecase.GetUpcomingEventsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * THIN ViewModel for Event Calendar screen.
 *
 * Observes repository state via use cases and delegates business logic to use cases.
 */
class EventCalendarViewModel(
    private val getUpcomingEventsUseCase: GetUpcomingEventsUseCase,
    private val filterEventsByCategoryUseCase: FilterEventsByCategoryUseCase
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)

    // Observe repository state via use case (don't copy!)
    val uiState: StateFlow<EventCalendarUiState> = combine(
        getUpcomingEventsUseCase.events,
        _selectedCategory,
        getUpcomingEventsUseCase.isLoading,
        getUpcomingEventsUseCase.error
    ) { allEvents, selectedCategory, isLoading, error ->
        when {
            error != null -> EventCalendarUiState.Error(error)
            allEvents.isNotEmpty() || !isLoading -> {
                // Filter events by category if selected
                val filteredEvents = if (selectedCategory != null) {
                    allEvents.filter { event ->
                        event.category.equals(selectedCategory, ignoreCase = true)
                    }
                } else {
                    allEvents
                }

                // Extract unique categories
                val categories = allEvents.map { it.category }.distinct().sorted()

                EventCalendarUiState.Success(
                    events = filteredEvents,
                    selectedCategory = selectedCategory,
                    availableCategories = categories,
                    isRefreshing = isLoading,
                    error = null
                )
            }
            else -> EventCalendarUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EventCalendarUiState.Loading
    )

    init {
        loadEvents()
    }

    /**
     * Load upcoming events from repository.
     */
    fun loadEvents() {
        viewModelScope.launch {
            getUpcomingEventsUseCase()
        }
    }

    /**
     * Refresh events from remote source.
     */
    fun refresh() {
        viewModelScope.launch {
            getUpcomingEventsUseCase.refresh()
        }
    }

    /**
     * Filter events by category.
     *
     * @param category Category to filter by, or null to show all events.
     */
    fun filterByCategory(category: String?) {
        _selectedCategory.value = category
    }

    /**
     * Clear category filter and show all events.
     */
    fun clearFilter() {
        _selectedCategory.value = null
    }
}
