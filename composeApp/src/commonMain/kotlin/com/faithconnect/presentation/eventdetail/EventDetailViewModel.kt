package com.faithconnect.presentation.eventdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithconnect.core.network.ApiResult
import com.faithconnect.domain.repository.EventRepository
import com.faithconnect.domain.usecase.FetchEventLocationUseCase
import com.faithconnect.domain.usecase.GetEventDescriptionUseCase
import com.faithconnect.domain.usecase.OpenMapForLocationUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * THIN ViewModel for Event Detail screen.
 *
 * Observes repository state and delegates business logic to use cases.
 * Requires an eventId parameter to load the specific event.
 */
class EventDetailViewModel(
    private val eventId: String,
    private val repository: EventRepository,
    private val fetchEventLocationUseCase: FetchEventLocationUseCase,
    private val getEventDescriptionUseCase: GetEventDescriptionUseCase,
    private val openMapForLocationUseCase: OpenMapForLocationUseCase
) : ViewModel() {

    // Observe repository state (don't copy!)
    val uiState: StateFlow<EventDetailUiState> = combine(
        repository.selectedEvent,
        repository.isLoading,
        repository.error
    ) { selectedEvent, isLoading, error ->
        when {
            error != null -> EventDetailUiState.Error(error)
            selectedEvent != null && selectedEvent.id == eventId -> EventDetailUiState.Success(
                event = selectedEvent,
                isRefreshing = isLoading,
                error = null
            )
            !isLoading && selectedEvent == null -> EventDetailUiState.Error("Event not found")
            else -> EventDetailUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EventDetailUiState.Loading
    )

    init {
        loadEvent()
    }

    /**
     * Load event details by ID.
     */
    private fun loadEvent() {
        viewModelScope.launch {
            repository.selectEvent(eventId)
        }
    }

    /**
     * Fetch additional event location details.
     * This use case might enhance the location data if needed.
     */
    fun fetchEventLocation() {
        viewModelScope.launch {
            val currentState = uiState.value
            if (currentState is EventDetailUiState.Success && currentState.event != null) {
                fetchEventLocationUseCase(currentState.event.id)
            }
        }
    }

    /**
     * Get event description (might format or enhance the description).
     */
    fun getEventDescription(): String? {
        val currentState = uiState.value
        return if (currentState is EventDetailUiState.Success && currentState.event != null) {
            getEventDescriptionUseCase(currentState.event)
        } else {
            null
        }
    }

    /**
     * Open map application for the event location.
     */
    fun openMap() {
        viewModelScope.launch {
            val currentState = uiState.value
            if (currentState is EventDetailUiState.Success && currentState.event != null) {
                openMapForLocationUseCase(currentState.event.location)
            }
        }
    }

    /**
     * Clear the selected event when leaving the screen.
     */
    override fun onCleared() {
        super.onCleared()
        repository.clearSelection()
    }
}
