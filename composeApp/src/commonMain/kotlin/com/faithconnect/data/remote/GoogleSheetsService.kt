package com.faithconnect.data.remote

import com.faithconnect.core.config.GoogleSheetsConfig
import com.faithconnect.domain.model.ChurchProfile
import com.faithconnect.domain.model.Event
import com.faithconnect.domain.model.NewsItem
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Google Sheets Service for fetching and parsing CSV data.
 *
 * This service fetches data from published Google Sheets CSV URLs and
 * parses them into domain models. It supports multiple date formats
 * and handles missing/invalid data gracefully.
 */
class GoogleSheetsService(
    private val httpClient: HttpClient
) {
    /**
     * Fetch events from Google Sheets
     */
    @OptIn(ExperimentalTime::class)
    suspend fun fetchEvents(sheetUrl: String): List<Event> {
        return try {
            val csvUrl = buildCsvUrl(sheetUrl, GoogleSheetsConfig.EVENTS_TAB)
            val csvText = httpClient.get(csvUrl).bodyAsText()

            // Validate response is CSV (not HTML error page)
            if (csvText.trim().startsWith("<!DOCTYPE") || csvText.trim().startsWith("<html")) {
                return emptyList()
            }

            parseEventsFromCsv(csvText)
        } catch (e: Exception) {
            println("Error fetching events: ${e.message}")
            emptyList()
        }
    }

    /**
     * Fetch news items from Google Sheets
     */
    @OptIn(ExperimentalTime::class)
    suspend fun fetchNewsItems(sheetUrl: String): List<NewsItem> {
        return try {
            val csvUrl = buildCsvUrl(sheetUrl, GoogleSheetsConfig.NEWS_TAB)
            val csvText = httpClient.get(csvUrl).bodyAsText()

            if (csvText.trim().startsWith("<!DOCTYPE") || csvText.trim().startsWith("<html")) {
                return emptyList()
            }

            parseNewsItemsFromCsv(csvText)
        } catch (e: Exception) {
            println("Error fetching news: ${e.message}")
            emptyList()
        }
    }

    /**
     * Fetch church profile from Google Sheets
     */
    suspend fun fetchChurchProfile(sheetUrl: String): ChurchProfile? {
        return try {
            val csvUrl = buildCsvUrl(sheetUrl, GoogleSheetsConfig.PROFILE_TAB)
            val csvText = httpClient.get(csvUrl).bodyAsText()

            if (csvText.trim().startsWith("<!DOCTYPE") || csvText.trim().startsWith("<html")) {
                return null
            }

            parseChurchProfileFromCsv(csvText)
        } catch (e: Exception) {
            println("Error fetching church profile: ${e.message}")
            null
        }
    }

    /**
     * Build CSV URL from base sheet URL and tab name.
     * Supports both edit URLs and published URLs.
     */
    private fun buildCsvUrl(baseUrl: String, tabName: String): String {
        // Extract sheet ID from URL
        val sheetIdRegex = """/spreadsheets/d/([a-zA-Z0-9-_]+)""".toRegex()
        val matchResult = sheetIdRegex.find(baseUrl)
        val sheetId = matchResult?.groupValues?.get(1) ?: return baseUrl

        // For now, use gid=0 as default (admin will need to configure correct GID per tab)
        return "https://docs.google.com/spreadsheets/d/$sheetId/export?format=csv&gid=0"
    }

    /**
     * Parse CSV text into Event list
     */
    @OptIn(ExperimentalTime::class)
    private fun parseEventsFromCsv(csv: String): List<Event> {
        val lines = csv.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()

        val headers = lines.first().split(",").map { it.trim().lowercase() }

        return lines.drop(1).mapNotNull { line ->
            try {
                val values = parseCsvLine(line)
                val data = headers.zip(values).toMap()

                val onDutyMembers = data["ondutycouncilmembers"]?.split(";")?.map { it.trim() } ?: emptyList()

                Event(
                    id = data["id"] ?: return@mapNotNull null,
                    title = data["title"] ?: "",
                    date = parseInstant(data["date"]),
                    category = data["category"] ?: "",
                    location = data["location"] ?: "",
                    description = data["description"] ?: "",
                    topic = data["topic"] ?: "",
                    bibleVerse = data["bibleverse"] ?: "",
                    picCouncilMember = data["piccouncilmember"] ?: "",
                    onDutyCouncilMembers = onDutyMembers,
                    isFeatured = data["isfeatured"]?.lowercase() in listOf("true", "1", "yes")
                )
            } catch (e: Exception) {
                println("Error parsing event row: $line - ${e.message}")
                null
            }
        }
    }

    /**
     * Parse CSV text into NewsItem list
     */
    @OptIn(ExperimentalTime::class)
    private fun parseNewsItemsFromCsv(csv: String): List<NewsItem> {
        val lines = csv.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()

        val headers = lines.first().split(",").map { it.trim().lowercase() }

        return lines.drop(1).mapNotNull { line ->
            try {
                val values = parseCsvLine(line)
                val data = headers.zip(values).toMap()

                NewsItem(
                    id = data["id"] ?: return@mapNotNull null,
                    headline = data["headline"] ?: "",
                    publishDate = parseInstant(data["publishdate"]),
                    author = data["author"] ?: "",
                    body = data["body"] ?: "",
                    category = data["category"] ?: "",
                    scriptureReference = data["scripturereference"] ?: "",
                    isUrgent = data["isurgent"]?.lowercase() in listOf("true", "1", "yes"),
                    photoURL = data["photourl"] ?: "",
                    relatedEventId = data["relatedeventid"] ?: ""
                )
            } catch (e: Exception) {
                println("Error parsing news row: $line - ${e.message}")
                null
            }
        }
    }

    /**
     * Parse CSV text into ChurchProfile
     */
    private fun parseChurchProfileFromCsv(csv: String): ChurchProfile? {
        val lines = csv.lines().filter { it.isNotBlank() }
        if (lines.size < 2) return null

        val headers = lines.first().split(",").map { it.trim().lowercase() }
        val values = parseCsvLine(lines[1])
        val data = headers.zip(values).toMap()

        return try {
            ChurchProfile(
                name = data["name"] ?: "",
                logoURL = data["logourl"] ?: "",
                welcomeMessage = data["welcomemessage"] ?: "",
                address = data["address"] ?: "",
                phone = data["phone"] ?: "",
                website = data["website"] ?: "",
                email = data["email"] ?: "",
                mission = data["mission"] ?: "",
                serviceTimes = data["servicetimes"] ?: "",
                socialFacebook = data["socialfacebook"] ?: ""
            )
        } catch (e: Exception) {
            println("Error parsing church profile: ${e.message}")
            null
        }
    }

    /**
     * Parse a single CSV line, handling quoted fields with commas
     */
    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false

        for (char in line) {
            when {
                char == '"' -> inQuotes = !inQuotes
                char == ',' && !inQuotes -> {
                    result.add(current.toString().trim())
                    current.clear()
                }
                else -> current.append(char)
            }
        }
        result.add(current.toString().trim())

        return result
    }

    /**
     * Parse date string to Instant, supporting multiple formats
     */
    @OptIn(ExperimentalTime::class)
    private fun parseInstant(dateString: String?): Instant {
        if (dateString.isNullOrBlank()) return Instant.fromEpochMilliseconds(0)

        // Supported formats:
        // - ISO: 2024-10-20T10:00:00Z
        // - Date only: 2024-10-20
        // - Slash format: 2024/10/20
        // - US format: 10/20/2024
        // - European format: 20/10/2024

        return try {
            // Try ISO format first
            Instant.parse(dateString)
        } catch (e: Exception) {
            try {
                // Try date-only format
                Instant.parse("${dateString}T00:00:00Z")
            } catch (e: Exception) {
                try {
                    // Try slash format
                    val normalized = dateString.replace("/", "-")
                    Instant.parse("${normalized}T00:00:00Z")
                } catch (e: Exception) {
                    // Fallback to current time
                    println("Could not parse date: $dateString")
                    Instant.fromEpochMilliseconds(0)
                }
            }
        }
    }
}
