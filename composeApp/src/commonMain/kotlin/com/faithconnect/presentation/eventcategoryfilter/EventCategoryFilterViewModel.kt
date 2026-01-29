package com.faithconnect.presentation.eventcategoryfilter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithconnect.domain.repository.EventRepository
import com.faithconnect.domain.usecase.ApplyCategoryFilterUseCase
import com.faithconnect.domain.usecase.ResetFiltersUseCase
import com.faithconnect.domain.usecase.SavePreferredCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * THIN ViewModel for Event Category Filter screen.
 *
 * Observes repository state and delegates business logic to use cases.
 */
class EventCategoryFilterViewModel(
    private val eventRepository: EventRepository,
    private val applyCategoryFilterUseCase: ApplyCategoryFilterUseCase,
    private val savePreferredCategoriesUseCase: SavePreferredCategoriesUseCase,
    private val resetFiltersUseCase: ResetFiltersUseCase
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    private val _selectedCategories = MutableStateFlow<Set<String>>(emptySet())

    // Observe repository state (don't copy!)
    val uiState: StateFlow<EventCategoryFilterUiState> = combine(
        eventRepository.events,
        _selectedCategories,
        _isSaving,
        eventRepository.error
    ) { events, selectedCategories, isSaving, error ->
        when {
            error != null -> EventCategoryFilterUiState.Error(error)
            events.isNotEmpty() -> {
                // Extract unique categories from events
                val availableCategories = events.map { it.category }.distinct().sorted()

                EventCategoryFilterUiState.Success(
                    availableCategories = availableCategories,
                    selectedCategories = selectedCategories,
                    isSaving = isSaving,
                    error = null
                )
            }
            else -> EventCategoryFilterUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EventCategoryFilterUiState.Loading
    )

    init {
        loadCategories()
        loadSavedPreferences()
    }

    /**
     * Load categories from events.
     */
    private fun loadCategories() {
        viewModelScope.launch {
            eventRepository.loadEvents()
        }
    }

    /**
     * Load saved category preferences.
     */
    private fun loadSavedPreferences() {
        viewModelScope.launch {
            val savedCategories = applyCategoryFilterUseCase.getSavedCategories()
            _selectedCategories.value = savedCategories
        }
    }

    /**
     * Toggle category selection.
     *
     * @param category Category to toggle.
     */
    fun toggleCategory(category: String) {
        _selectedCategories.update { current ->
            if (current.contains(category)) {
                current - category
            } else {
                current + category
            }
        }
    }

    /**
     * Apply the selected category filters.
     *
     * @param categories Set of categories to filter by.
     */
    fun applyFilter(categories: Set<String>) {
        viewModelScope.launch {
            applyCategoryFilterUseCase(categories)
            _selectedCategories.value = categories
        }
    }

    /**
     * Save selected categories as preferences.
     */
    fun savePreferences() {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                savePreferredCategoriesUseCase(_selectedCategories.value)
            } finally {
                _isSaving.value = false
            }
        }
    }

    /**
     * Reset all filters to default (show all categories).
     */
    fun resetFilters() {
        viewModelScope.launch {
            resetFiltersUseCase()
            _selectedCategories.value = emptySet()
        }
    }
}
