package com.faithconnect.core.config

/**
 * Google Sheets configuration
 * The sheet URL will be configured by users through the admin setup screen
 */
object GoogleSheetsConfig {
    // These will be dynamically configured by users
    // Default empty - user must set up through onboarding
    const val DEFAULT_SHEET_URL = ""

    // Refresh interval (30 minutes)
    const val REFRESH_INTERVAL_MINUTES = 30

    // Tab names for CSV fetching
    const val EVENTS_TAB = "Events"
    const val NEWS_TAB = "News"
    const val PROFILE_TAB = "ChurchProfile"
}
