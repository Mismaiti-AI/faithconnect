package com.faithconnect.domain.usecase

import com.faithconnect.core.network.ApiResult
import com.faithconnect.domain.model.Event
import com.faithconnect.domain.repository.EventRepository

/**
 * Use case for fetching event location details.
 *
 * Business logic: Retrieves a specific event and extracts its location information.
 */
class FetchEventLocationUseCase(
    private val repository: EventRepository
) {
    /**
     * Fetch event location by event ID.
     *
     * @param eventId The ID of the event to fetch location for.
     * @return ApiResult containing the event (with location data).
     */
    suspend operator fun invoke(eventId: String): ApiResult<Event> {
        return repository.getEventById(eventId)
    }

    /**
     * Get location string from event.
     *
     * @param event The event to extract location from.
     * @return The location string, or a default message if empty.
     */
    fun getLocationString(event: Event): String {
        return if (event.location.isNotBlank()) {
            event.location
        } else {
            "Location not specified"
        }
    }

    /**
     * Check if event has a valid location.
     *
     * @param event The event to check.
     * @return True if event has a non-empty location string.
     */
    fun hasLocation(event: Event): Boolean {
        return event.location.isNotBlank()
    }
}
