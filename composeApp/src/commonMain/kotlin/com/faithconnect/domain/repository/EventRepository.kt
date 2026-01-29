package com.faithconnect.domain.repository

import com.faithconnect.core.network.ApiResult
import com.faithconnect.domain.model.Event
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for Event data operations.
 *
 * Provides shared state management for events across multiple screens.
 * All ViewModels observe the same StateFlow instances from this repository.
 */
interface EventRepository {
    // Shared state - observable by all ViewModels
    val events: StateFlow<List<Event>>
    val selectedEvent: StateFlow<Event?>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>

    // Actions - called by ViewModels/UseCases
    suspend fun loadEvents(): ApiResult<Unit>
    suspend fun refreshEvents(): ApiResult<Unit>
    suspend fun selectEvent(eventId: String)
    fun clearSelection()
    suspend fun getEventById(id: String): ApiResult<Event>
    fun clearError()
}
