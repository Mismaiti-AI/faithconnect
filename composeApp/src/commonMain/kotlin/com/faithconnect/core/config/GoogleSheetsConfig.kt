package com.faithconnect.core.config

/**
 * Configuration for Google Sheets data source.
 *
 * This app uses Google Sheets as a simple CMS (Content Management System).
 * The church admin updates content in Google Sheets, and the app fetches
 * the data via published CSV URLs.
 *
 * Setup Instructions:
 * 1. Create Google Sheets with tabs for Events, News, and Church Profile
 * 2. File → Share → Publish to web
 * 3. For each tab, publish as CSV format
 * 4. Copy the published URLs and paste them below
 * 5. URLs should look like: https://docs.google.com/spreadsheets/d/{SHEET_ID}/export?format=csv&gid={TAB_GID}
 */
object GoogleSheetsConfig {
    // Default Google Sheets URL - to be configured by admin during onboarding
    var sheetsBaseUrl: String = ""

    // Refresh interval (30 minutes)
    const val REFRESH_INTERVAL_MINUTES = 30

    // Tab names in the Google Sheet
    const val EVENTS_TAB = "Events"
    const val NEWS_TAB = "News"
    const val PROFILE_TAB = "ChurchProfile"
}
