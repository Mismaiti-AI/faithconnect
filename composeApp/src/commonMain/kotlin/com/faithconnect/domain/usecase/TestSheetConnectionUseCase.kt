package com.faithconnect.domain.usecase

import com.faithconnect.data.remote.GoogleSheetsService
import com.faithconnect.domain.repository.PreferencesRepository

/**
 * Use case for testing Google Sheets connection.
 *
 * Business logic: Attempts to fetch data from the configured Google Sheets URL
 * to verify connectivity and data availability.
 */
class TestSheetConnectionUseCase(
    private val googleSheetsService: GoogleSheetsService,
    private val preferencesRepository: PreferencesRepository
) {
    /**
     * Test connection result.
     */
    data class ConnectionTestResult(
        val isSuccessful: Boolean,
        val message: String,
        val eventsCount: Int = 0,
        val newsCount: Int = 0,
        val hasProfile: Boolean = false
    )

    /**
     * Test the Google Sheets connection.
     *
     * Business logic: Attempts to fetch data from all tabs (events, news, profile)
     * and reports on what was found.
     *
     * @param url Optional URL to test. If null, uses the saved URL from preferences.
     * @return ConnectionTestResult with detailed information about the test.
     */
    suspend operator fun invoke(url: String? = null): ConnectionTestResult {
        // Get URL to test
        val testUrl = url ?: preferencesRepository.getSheetUrl()

        if (testUrl.isNullOrBlank()) {
            return ConnectionTestResult(
                isSuccessful = false,
                message = "No Google Sheets URL configured"
            )
        }

        return try {
            // Test fetching events
            val events = googleSheetsService.fetchEvents(testUrl)
            val news = googleSheetsService.fetchNewsItems(testUrl)
            val profile = googleSheetsService.fetchChurchProfile(testUrl)

            // Check if we got any data
            val hasData = events.isNotEmpty() || news.isNotEmpty() || profile != null

            if (hasData) {
                val details = buildString {
                    append("Connection successful!")
                    if (events.isNotEmpty()) append("\n- Found ${events.size} events")
                    if (news.isNotEmpty()) append("\n- Found ${news.size} news items")
                    if (profile != null) append("\n- Found church profile")
                }

                ConnectionTestResult(
                    isSuccessful = true,
                    message = details,
                    eventsCount = events.size,
                    newsCount = news.size,
                    hasProfile = profile != null
                )
            } else {
                ConnectionTestResult(
                    isSuccessful = false,
                    message = "Connection successful but no data found. Please check:\n" +
                            "- Sheet is published to the web\n" +
                            "- Tab names match expected format\n" +
                            "- Data is properly formatted with headers"
                )
            }
        } catch (e: Exception) {
            ConnectionTestResult(
                isSuccessful = false,
                message = "Connection failed: ${e.message ?: "Unknown error"}"
            )
        }
    }

    /**
     * Quick connection test (just checks if URL is reachable).
     *
     * @param url URL to test.
     * @return True if connection succeeds, false otherwise.
     */
    suspend fun quickTest(url: String): Boolean {
        return try {
            val events = googleSheetsService.fetchEvents(url)
            true // Connection worked (even if no data)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Test if a specific tab is accessible.
     *
     * @param url Google Sheets URL.
     * @param tabType Type of tab to test (events, news, or profile).
     * @return True if tab is accessible and has data.
     */
    suspend fun testTab(url: String, tabType: TabType): Boolean {
        return try {
            when (tabType) {
                TabType.EVENTS -> googleSheetsService.fetchEvents(url).isNotEmpty()
                TabType.NEWS -> googleSheetsService.fetchNewsItems(url).isNotEmpty()
                TabType.PROFILE -> googleSheetsService.fetchChurchProfile(url) != null
            }
        } catch (e: Exception) {
            false
        }
    }

    enum class TabType {
        EVENTS, NEWS, PROFILE
    }
}
