package com.faithconnect.presentation.adminconfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithconnect.core.network.ApiResult
import com.faithconnect.domain.usecase.SetGoogleSheetUrlUseCase
import com.faithconnect.domain.usecase.TestSheetConnectionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * THIN ViewModel for Admin Configuration screen.
 *
 * Delegates business logic to use cases. Exposes UI state as StateFlow.
 */
class AdminConfigViewModel(
    private val setGoogleSheetUrlUseCase: SetGoogleSheetUrlUseCase,
    private val testSheetConnectionUseCase: TestSheetConnectionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminConfigUiState>(AdminConfigUiState.Loading)
    val uiState: StateFlow<AdminConfigUiState> = _uiState.asStateFlow()

    init {
        loadCurrentConfig()
    }

    /**
     * Load current configuration (if any).
     */
    private fun loadCurrentConfig() {
        viewModelScope.launch {
            try {
                val currentUrl = setGoogleSheetUrlUseCase.getCurrentUrl()
                _uiState.value = AdminConfigUiState.Success(
                    sheetUrl = currentUrl ?: "",
                    isValidUrl = currentUrl != null && setGoogleSheetUrlUseCase.validateUrl(currentUrl) == null
                )
            } catch (e: Exception) {
                _uiState.value = AdminConfigUiState.Error(
                    message = "Failed to load configuration: ${e.message}"
                )
            }
        }
    }

    /**
     * Set the Google Sheets URL.
     *
     * @param url The Google Sheets URL to set.
     */
    fun setSheetUrl(url: String) {
        val currentState = _uiState.value
        if (currentState is AdminConfigUiState.Success) {
            val validationError = setGoogleSheetUrlUseCase.validateUrl(url)
            _uiState.update {
                currentState.copy(
                    sheetUrl = url,
                    isValidUrl = validationError == null,
                    error = validationError
                )
            }
        }
    }

    /**
     * Test connection to the configured Google Sheets URL.
     */
    fun testConnection() {
        val currentState = _uiState.value
        if (currentState !is AdminConfigUiState.Success) return

        viewModelScope.launch {
            _uiState.update { currentState.copy(isTestingConnection = true, connectionTestResult = null) }

            try {
                val result = testSheetConnectionUseCase(currentState.sheetUrl)
                _uiState.update {
                    (it as AdminConfigUiState.Success).copy(
                        isTestingConnection = false,
                        connectionTestResult = if (result.isSuccessful) {
                            ConnectionTestResult.Success
                        } else {
                            ConnectionTestResult.Failure(result.message)
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    (it as AdminConfigUiState.Success).copy(
                        isTestingConnection = false,
                        connectionTestResult = ConnectionTestResult.Failure(e.message ?: "Unknown error")
                    )
                }
            }
        }
    }

    /**
     * Save the configuration.
     */
    fun saveConfig() {
        val currentState = _uiState.value
        if (currentState !is AdminConfigUiState.Success) return
        if (!currentState.isValidUrl) {
            _uiState.update { currentState.copy(error = "Cannot save invalid URL") }
            return
        }

        viewModelScope.launch {
            _uiState.update { currentState.copy(isSaving = true, error = null) }

            try {
                val result = setGoogleSheetUrlUseCase(currentState.sheetUrl)
                result.fold(
                    onSuccess = {
                        _uiState.update {
                            (it as AdminConfigUiState.Success).copy(
                                isSaving = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            (it as AdminConfigUiState.Success).copy(
                                isSaving = false,
                                error = error.message ?: "Failed to save configuration"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    (it as AdminConfigUiState.Success).copy(
                        isSaving = false,
                        error = e.message ?: "Unknown error"
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
        if (currentState is AdminConfigUiState.Success) {
            _uiState.update { currentState.copy(error = null) }
        }
    }
}
