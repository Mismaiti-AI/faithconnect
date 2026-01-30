package com.faithconnect.domain.usecase

import com.faithconnect.core.network.ApiResult
import com.faithconnect.domain.model.Event
import com.faithconnect.domain.repository.EventRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Use case for retrieving upcoming events.
 *
 * Business logic: Filters events to show only those with dates in the future,
 * sorted by date (nearest first).
 */
@OptIn(ExperimentalTime::class)
class GetUpcomingEventsUseCase(
    private val repository: EventRepository
) {
    // Expose repository state (observe, don't copy!)
    val events: StateFlow<List<Event>> = repository.events
    val isLoading: StateFlow<Boolean> = repository.isLoading
    val error: StateFlow<String?> = repository.error

    /**
     * Load upcoming events from repository.
     * Business logic: Only return events with dates in the future.
     */
    suspend operator fun invoke(): ApiResult<Unit> {
        return repository.loadEvents()
    }

    /**
     * Refresh upcoming events from remote source.
     */
    suspend fun refresh(): ApiResult<Unit> {
        return repository.refreshEvents()
    }

    /**
     * Get filtered list of upcoming events (future dates only).
     * Returns a Flow that filters events based on current time.
     */
    fun getUpcomingEvents() = repository.events.map { allEvents ->
        val now = Clock.System.now()
        allEvents
            .filter { event -> event.date > now }
            .sortedBy { it.date }
    }
}
