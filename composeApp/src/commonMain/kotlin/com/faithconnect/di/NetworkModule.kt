package com.faithconnect.di

import com.faithconnect.data.remote.GoogleSheetsService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

/**
 * Network module for HTTP client and remote services.
 *
 * Provides:
 * - HttpClient (singleton, shared across app)
 * - GoogleSheetsService (singleton, CSV fetching)
 */
val networkModule = module {
    // ========================================
    // HTTP CLIENT (Singleton)
    // ========================================
    // Single HttpClient instance shared across all network requests
    // Platform-specific engine provided by expect/actual in platformModule

    single {
        HttpClient {
            // Content negotiation for JSON (even though we use CSV, good to have)
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                    isLenient = true
                    prettyPrint = false
                })
            }

            // Logging for debugging network requests
            install(Logging) {
                level = LogLevel.INFO
            }

            // Timeout configuration
            install(HttpTimeout) {
                requestTimeoutMillis = 30000  // 30 seconds
                connectTimeoutMillis = 30000  // 30 seconds
                socketTimeoutMillis = 30000   // 30 seconds
            }
        }
    }

    // ========================================
    // REMOTE SERVICES (Singletons)
    // ========================================

    single {
        GoogleSheetsService(
            httpClient = get()
        )
    }
}
