package com.faithconnect.domain.usecase

import com.faithconnect.domain.model.Event

/**
 * Use case for opening map application with event location.
 *
 * Business logic: Generates map URLs for different platforms (Google Maps, Apple Maps)
 * based on the event location. Platform-specific launcher should be injected to handle
 * the actual URL opening.
 */
class OpenMapForLocationUseCase {
    /**
     * Generate Google Maps URL for event location.
     *
     * @param event Event with location information.
     * @return URL string for Google Maps, or null if no location.
     */
    fun getGoogleMapsUrl(event: Event): String? {
        return if (event.location.isNotBlank()) {
            val encodedLocation = event.location.replace(" ", "+")
            "https://www.google.com/maps/search/?api=1&query=$encodedLocation"
        } else {
            null
        }
    }

    /**
     * Generate Apple Maps URL for event location.
     *
     * @param event Event with location information.
     * @return URL string for Apple Maps, or null if no location.
     */
    fun getAppleMapsUrl(event: Event): String? {
        return if (event.location.isNotBlank()) {
            val encodedLocation = event.location.replace(" ", "+")
            "https://maps.apple.com/?q=$encodedLocation"
        } else {
            null
        }
    }

    /**
     * Generate generic maps URL (works on both platforms).
     *
     * @param event Event with location information.
     * @return URL string for maps, or null if no location.
     */
    operator fun invoke(event: Event): String? {
        return getGoogleMapsUrl(event)
    }

    /**
     * Check if event has a location that can be opened in maps.
     *
     * @param event Event to check.
     * @return True if event has a valid location.
     */
    fun canOpenMap(event: Event): Boolean {
        return event.location.isNotBlank()
    }

    /**
     * Get directions URL from a starting location to event location.
     *
     * @param event Event with destination location.
     * @param fromLocation Starting location (optional - uses current location if null).
     * @return URL string for directions.
     */
    fun getDirectionsUrl(event: Event, fromLocation: String? = null): String? {
        if (event.location.isBlank()) return null

        val encodedDestination = event.location.replace(" ", "+")

        return if (fromLocation != null) {
            val encodedOrigin = fromLocation.replace(" ", "+")
            "https://www.google.com/maps/dir/?api=1&origin=$encodedOrigin&destination=$encodedDestination"
        } else {
            // Use current location as origin
            "https://www.google.com/maps/dir/?api=1&destination=$encodedDestination"
        }
    }
}
