package com.faithconnect.domain.usecase

import com.faithconnect.core.network.ApiResult
import com.faithconnect.domain.model.Event
import com.faithconnect.domain.repository.EventRepository

/**
 * Use case for retrieving event description and related details.
 *
 * Business logic: Fetches event details including description, topic,
 * bible verse, and council member information.
 */
class GetEventDescriptionUseCase(
    private val repository: EventRepository
) {
    /**
     * Get event details by ID.
     *
     * @param eventId The ID of the event to fetch.
     * @return ApiResult containing the event with full description.
     */
    suspend operator fun invoke(eventId: String): ApiResult<Event> {
        return repository.getEventById(eventId)
    }

    /**
     * Format event description with all relevant details.
     *
     * Business logic: Combines description, topic, and bible verse
     * into a formatted string for display.
     *
     * @param event The event to format.
     * @return Formatted description string.
     */
    fun getFormattedDescription(event: Event): String {
        val parts = mutableListOf<String>()

        // Add main description
        if (event.description.isNotBlank()) {
            parts.add(event.description)
        }

        // Add topic if available
        if (event.topic.isNotBlank()) {
            parts.add("\nTopic: ${event.topic}")
        }

        // Add bible verse if available
        if (event.bibleVerse.isNotBlank()) {
            parts.add("\nScripture: ${event.bibleVerse}")
        }

        // Add PIC if available
        if (event.picCouncilMember.isNotBlank()) {
            parts.add("\nPerson In Charge: ${event.picCouncilMember}")
        }

        // Add on-duty members if available
        if (event.onDutyCouncilMembers.isNotEmpty()) {
            parts.add("\nOn-Duty Council Members: ${event.onDutyCouncilMembers.joinToString(", ")}")
        }

        return if (parts.isNotEmpty()) {
            parts.joinToString("\n")
        } else {
            "No description available"
        }
    }

    /**
     * Check if event has a description.
     *
     * @param event The event to check.
     * @return True if event has a non-empty description.
     */
    fun hasDescription(event: Event): Boolean {
        return event.description.isNotBlank()
    }

    /**
     * Get summary of event (title + short description).
     *
     * @param event The event to summarize.
     * @param maxLength Maximum length of summary (defaults to 100 characters).
     * @return Brief summary of the event.
     */
    fun getSummary(event: Event, maxLength: Int = 100): String {
        val desc = event.description.ifBlank { event.topic }
        return if (desc.length > maxLength) {
            desc.take(maxLength - 3) + "..."
        } else {
            desc
        }
    }
}
