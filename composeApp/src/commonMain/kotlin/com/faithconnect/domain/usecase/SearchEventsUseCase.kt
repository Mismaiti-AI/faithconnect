package com.faithconnect.domain.usecase

import com.faithconnect.domain.model.Event
import com.faithconnect.domain.repository.EventRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

/**
 * Use case for searching events by text query.
 *
 * Business logic: Searches across multiple event fields (title, description,
 * topic, location, category) to find matching events.
 */
class SearchEventsUseCase(
    private val repository: EventRepository
) {
    // Expose repository state (observe, don't copy!)
    val events: StateFlow<List<Event>> = repository.events
    val isLoading: StateFlow<Boolean> = repository.isLoading
    val error: StateFlow<String?> = repository.error

    /**
     * Search events by query text.
     *
     * Business logic: Searches across title, description, topic, location,
     * category, and bible verse fields (case-insensitive).
     *
     * @param query Search query string. Pass empty/null to return all events.
     * @return Flow of events matching the search query.
     */
    operator fun invoke(query: String?) = repository.events.map { allEvents ->
        if (query.isNullOrBlank()) {
            allEvents
        } else {
            val searchQuery = query.trim().lowercase()
            allEvents.filter { event ->
                event.title.lowercase().contains(searchQuery) ||
                event.description.lowercase().contains(searchQuery) ||
                event.topic.lowercase().contains(searchQuery) ||
                event.location.lowercase().contains(searchQuery) ||
                event.category.lowercase().contains(searchQuery) ||
                event.bibleVerse.lowercase().contains(searchQuery)
            }
        }
    }

    /**
     * Search events by query with additional filters.
     *
     * @param query Search query string.
     * @param categories Optional list of categories to restrict search.
     * @param featuredOnly If true, only return featured events.
     * @return Flow of filtered and searched events.
     */
    fun searchWithFilters(
        query: String?,
        categories: List<String>? = null,
        featuredOnly: Boolean = false
    ) = repository.events.map { allEvents ->
        var filtered = allEvents

        // Apply search query
        if (!query.isNullOrBlank()) {
            val searchQuery = query.trim().lowercase()
            filtered = filtered.filter { event ->
                event.title.lowercase().contains(searchQuery) ||
                event.description.lowercase().contains(searchQuery) ||
                event.topic.lowercase().contains(searchQuery) ||
                event.location.lowercase().contains(searchQuery) ||
                event.category.lowercase().contains(searchQuery) ||
                event.bibleVerse.lowercase().contains(searchQuery)
            }
        }

        // Apply category filter
        if (!categories.isNullOrEmpty()) {
            filtered = filtered.filter { event ->
                categories.any { category ->
                    event.category.equals(category, ignoreCase = true)
                }
            }
        }

        // Apply featured filter
        if (featuredOnly) {
            filtered = filtered.filter { it.isFeatured }
        }

        filtered
    }
}
