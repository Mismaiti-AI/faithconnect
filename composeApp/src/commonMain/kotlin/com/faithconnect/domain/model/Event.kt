package com.faithconnect.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Event domain model representing church activities, seminars, and workshops.
 *
 * This model contains all information needed to display and manage events
 * in the FaithConnect app.
 */
@OptIn(ExperimentalTime::class)
data class Event(
    val id: String = "",
    val title: String = "",
    val date: Instant = Instant.ZERO,
    val category: String = "",
    val location: String = "",
    val description: String = "",
    val topic: String = "",
    val bibleVerse: String = "",
    val picCouncilMember: String = "",
    val onDutyCouncilMembers: List<String> = emptyList(),
    val isFeatured: Boolean = false
)
