package com.faithconnect.domain.usecase

import com.faithconnect.domain.model.Event
import com.faithconnect.domain.repository.EventRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

/**
 * Use case for filtering events by category.
 *
 * Business logic: Filters the event list by the specified category
 * (e.g., "Youth", "Women's Ministry", "Outreach").
 */
class FilterEventsByCategoryUseCase(
    private val repository: EventRepository
) {
    // Expose repository state (observe, don't copy!)
    val events: StateFlow<List<Event>> = repository.events
    val isLoading: StateFlow<Boolean> = repository.isLoading
    val error: StateFlow<String?> = repository.error

    /**
     * Filter events by category.
     *
     * @param category The category to filter by (case-insensitive).
     *                 Pass empty string or null to return all events.
     * @return Flow of filtered events.
     */
    operator fun invoke(category: String?) = repository.events.map { allEvents ->
        if (category.isNullOrBlank()) {
            allEvents
        } else {
            allEvents.filter { event ->
                event.category.equals(category, ignoreCase = true)
            }
        }
    }

    /**
     * Filter events by multiple categories.
     *
     * @param categories List of categories to filter by (case-insensitive).
     *                   Pass empty list to return all events.
     * @return Flow of filtered events.
     */
    fun filterByCategories(categories: List<String>) = repository.events.map { allEvents ->
        if (categories.isEmpty()) {
            allEvents
        } else {
            allEvents.filter { event ->
                categories.any { category ->
                    event.category.equals(category, ignoreCase = true)
                }
            }
        }
    }

    /**
     * Get unique list of all categories from events.
     * Useful for displaying filter options.
     */
    fun getAllCategories() = repository.events.map { allEvents ->
        allEvents
            .map { it.category }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }
}
