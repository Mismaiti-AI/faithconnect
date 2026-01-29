package com.faithconnect.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * NewsItem domain model representing church announcements, pastoral updates,
 * and community highlights.
 *
 * This model contains all information needed to display news articles
 * and announcements in the FaithConnect app.
 */
@OptIn(ExperimentalTime::class)
data class NewsItem(
    val id: String = "",
    val headline: String = "",
    val publishDate: Instant = Instant.ZERO,
    val author: String = "",
    val body: String = "",
    val category: String = "",
    val scriptureReference: String = "",
    val isUrgent: Boolean = false,
    val photoURL: String = "",
    val relatedEventId: String = ""
)
