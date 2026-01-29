package com.faithconnect.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using @Serializable
 *
 * Simple routes use 'object' (no parameters)
 * Parameterized routes use 'data class' (with parameters)
 */

// ═══════════════════════════════════════════════════════════════
// SIMPLE ROUTES (No Parameters)
// ═══════════════════════════════════════════════════════════════

@Serializable
object Home

@Serializable
object OnboardingSetup

@Serializable
object EventCalendar

@Serializable
object EventCategoryFilter

@Serializable
object NewsFeed

@Serializable
object ChurchProfile

@Serializable
object Settings

@Serializable
object AdminConfig

// ═══════════════════════════════════════════════════════════════
// PARAMETERIZED ROUTES (With Parameters)
// ═══════════════════════════════════════════════════════════════

@Serializable
data class EventDetail(val eventId: String)

@Serializable
data class NewsDetail(val newsId: String)
