package com.faithconnect.data.repository

import com.faithconnect.core.network.ApiResult
import com.faithconnect.data.local.dao.EventDao
import com.faithconnect.data.local.entity.toDomain
import com.faithconnect.data.local.entity.toEntity
import com.faithconnect.data.remote.GoogleSheetsService
import com.faithconnect.domain.model.Event
import com.faithconnect.domain.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Implementation of EventRepository using Google Sheets as remote source
 * and Room database for offline caching.
 *
 * This repository owns the state and provides it to all ViewModels.
 * ViewModels should observe these StateFlows, not create their own copies.
 */
class EventRepositoryImpl(
    private val googleSheetsService: GoogleSheetsService,
    private val eventDao: EventDao,
    private val sheetUrlProvider: () -> String
) : EventRepository {

    // Private mutable state
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    private val _selectedEvent = MutableStateFlow<Event?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    // Public immutable state (observable by ViewModels)
    override val events: StateFlow<List<Event>> = _events.asStateFlow()
    override val selectedEvent: StateFlow<Event?> = _selectedEvent.asStateFlow()
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    override val error: StateFlow<String?> = _error.asStateFlow()

    // Repository scope for background sync
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        // Observe local database and auto-sync to StateFlow
        repositoryScope.launch {
            eventDao.observeAll().collect { entities ->
                _events.value = entities.map { it.toDomain() }
            }
        }
    }

    override suspend fun loadEvents(): ApiResult<Unit> {
        // Return cached if available
        if (_events.value.isNotEmpty()) {
            return ApiResult.Success(Unit)
        }
        return refreshEvents()
    }

    override suspend fun refreshEvents(): ApiResult<Unit> {
        _isLoading.value = true
        _error.value = null

        return try {
            val sheetUrl = sheetUrlProvider()
            if (sheetUrl.isBlank()) {
                _error.value = "Google Sheets URL not configured"
                _isLoading.value = false
                return ApiResult.Error(Exception("Sheet URL not configured"))
            }

            val events = googleSheetsService.fetchEvents(sheetUrl)

            // Persist to DB (auto-syncs to StateFlow via observeAll)
            eventDao.replaceAll(events.map { it.toEntity() })

            _isLoading.value = false
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false

            // Try fallback to cache
            val cached = eventDao.getAll()
            if (cached.isNotEmpty()) {
                _events.value = cached.map { it.toDomain() }
                _error.value = "Using cached data. ${e.message}"
                ApiResult.Success(Unit)
            } else {
                _error.value = e.message ?: "Failed to load events"
                ApiResult.Error(e)
            }
        }
    }

    override suspend fun selectEvent(eventId: String) {
        // First check in-memory
        val event = _events.value.find { it.id == eventId }
        if (event != null) {
            _selectedEvent.value = event
            return
        }

        // Check database
        val cached = eventDao.getById(eventId)
        if (cached != null) {
            _selectedEvent.value = cached.toDomain()
        } else {
            _error.value = "Event not found: $eventId"
        }
    }

    override fun clearSelection() {
        _selectedEvent.value = null
    }

    override suspend fun getEventById(id: String): ApiResult<Event> {
        // Check in-memory first
        val cached = _events.value.find { it.id == id }
        if (cached != null) {
            return ApiResult.Success(cached)
        }

        // Check local DB
        val local = eventDao.getById(id)
        if (local != null) {
            return ApiResult.Success(local.toDomain())
        }

        return ApiResult.Error(Exception("Event not found: $id"))
    }

    override fun clearError() {
        _error.value = null
    }
}
