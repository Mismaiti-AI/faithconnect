package com.faithconnect.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithconnect.domain.repository.PreferencesRepository
import com.faithconnect.domain.usecase.SavePreferredCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * THIN ViewModel for Settings screen.
 *
 * Observes preferences repository and delegates business logic to use cases.
 */
class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val savePreferredCategoriesUseCase: SavePreferredCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    /**
     * Load current settings from preferences.
     */
    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val sheetUrl = preferencesRepository.getSheetUrl()
                // In a real app, we'd load preferred categories and dark mode preference
                _uiState.value = SettingsUiState.Success(
                    sheetUrl = sheetUrl,
                    preferredCategories = emptySet(), // Would load from preferences
                    isDarkMode = false // Would load from preferences
                )
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(
                    message = "Failed to load settings: ${e.message}"
                )
            }
        }
    }

    /**
     * Update preferred categories.
     *
     * @param categories Set of preferred categories.
     */
    fun updatePreferredCategories(categories: Set<String>) {
        val currentState = _uiState.value
        if (currentState !is SettingsUiState.Success) return

        viewModelScope.launch {
            _uiState.update { currentState.copy(isSaving = true, error = null) }

            try {
                savePreferredCategoriesUseCase(categories)
                _uiState.update {
                    (it as SettingsUiState.Success).copy(
                        preferredCategories = categories,
                        isSaving = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    (it as SettingsUiState.Success).copy(
                        isSaving = false,
                        error = "Failed to save preferences: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Toggle a specific category in preferred categories.
     *
     * @param category Category to toggle.
     */
    fun togglePreferredCategory(category: String) {
        val currentState = _uiState.value
        if (currentState !is SettingsUiState.Success) return

        val newCategories = if (currentState.preferredCategories.contains(category)) {
            currentState.preferredCategories - category
        } else {
            currentState.preferredCategories + category
        }

        updatePreferredCategories(newCategories)
    }

    /**
     * Toggle dark mode preference.
     *
     * @param enabled Whether dark mode should be enabled.
     */
    fun toggleDarkMode(enabled: Boolean) {
        val currentState = _uiState.value
        if (currentState is SettingsUiState.Success) {
            _uiState.update { currentState.copy(isDarkMode = enabled) }
            // In a real app, would save to preferences
        }
    }

    /**
     * Clear app cache.
     */
    fun clearCache() {
        val currentState = _uiState.value
        if (currentState !is SettingsUiState.Success) return

        viewModelScope.launch {
            _uiState.update { currentState.copy(isClearingCache = true, error = null) }

            try {
                // In a real app, would clear Room database cache and remote cache
                // For now, just simulate the operation
                kotlinx.coroutines.delay(500)
                _uiState.update {
                    (it as SettingsUiState.Success).copy(isClearingCache = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    (it as SettingsUiState.Success).copy(
                        isClearingCache = false,
                        error = "Failed to clear cache: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Reset all settings to defaults.
     */
    fun resetToDefaults() {
        val currentState = _uiState.value
        if (currentState !is SettingsUiState.Success) return

        viewModelScope.launch {
            _uiState.update { currentState.copy(isSaving = true, error = null) }

            try {
                preferencesRepository.clear()
                _uiState.update {
                    (it as SettingsUiState.Success).copy(
                        preferredCategories = emptySet(),
                        isDarkMode = false,
                        isSaving = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    (it as SettingsUiState.Success).copy(
                        isSaving = false,
                        error = "Failed to reset settings: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        val currentState = _uiState.value
        if (currentState is SettingsUiState.Success) {
            _uiState.update { currentState.copy(error = null) }
        }
    }
}
