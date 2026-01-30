package com.faithconnect.presentation.onboarding

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
 * THIN ViewModel for Onboarding screen.
 *
 * Delegates business logic to use cases. Exposes UI state as StateFlow.
 * Checks existing state in init to skip onboarding if already configured.
 */
class OnboardingViewModel(
    private val setGoogleSheetUrlUseCase: SetGoogleSheetUrlUseCase,
    private val testSheetConnectionUseCase: TestSheetConnectionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Loading)
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        checkExistingConfiguration()
    }

    /**
     * Check if app is already configured.
     * If configured, mark as success to skip onboarding.
     */
    private fun checkExistingConfiguration() {
        viewModelScope.launch {
            try {
                val isConfigured = setGoogleSheetUrlUseCase.isConfigured()
                if (isConfigured) {
                    // Already configured, skip onboarding
                    _uiState.value = OnboardingUiState.Ready(isSuccess = true)
                } else {
                    // Not configured, show onboarding
                    _uiState.value = OnboardingUiState.Ready()
                }
            } catch (e: Exception) {
                _uiState.value = OnboardingUiState.Ready()
            }
        }
    }

    /**
     * Validate and save Google Sheets URL.
     *
     * @param url The Google Sheets URL to validate and save.
     */
    fun validateAndSaveUrl(url: String) {
        val currentState = _uiState.value
        if (currentState !is OnboardingUiState.Ready) return

        // Update URL and validate
        val validationError = setGoogleSheetUrlUseCase.validateUrl(url)
        _uiState.update {
            currentState.copy(
                sheetUrl = url,
                isValidUrl = validationError == null,
                error = validationError
            )
        }

        // If valid, save automatically
        if (validationError == null && url.isNotBlank()) {
            saveUrl(url)
        }
    }

    /**
     * Save the Google Sheets URL.
     */
    private fun saveUrl(url: String) {
        val currentState = _uiState.value
        if (currentState !is OnboardingUiState.Ready) return

        viewModelScope.launch {
            _uiState.update { currentState.copy(isSaving = true, error = null) }

            try {
                val result = setGoogleSheetUrlUseCase(url)
                result.fold(
                    onSuccess = {
                        _uiState.update {
                            (it as OnboardingUiState.Ready).copy(
                                isSaving = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            (it as OnboardingUiState.Ready).copy(
                                isSaving = false,
                                error = error.message ?: "Failed to save URL"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    (it as OnboardingUiState.Ready).copy(
                        isSaving = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    /**
     * Test connection to the Google Sheets URL.
     */
    fun testConnection() {
        val currentState = _uiState.value
        if (currentState !is OnboardingUiState.Ready) return
        if (!currentState.isValidUrl) {
            _uiState.update { currentState.copy(error = "Please enter a valid URL first") }
            return
        }

        viewModelScope.launch {
            _uiState.update { currentState.copy(isTestingConnection = true, connectionTestResult = null) }

            try {
                val result = testSheetConnectionUseCase(currentState.sheetUrl)
                _uiState.update {
                    (it as OnboardingUiState.Ready).copy(
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
                    (it as OnboardingUiState.Ready).copy(
                        isTestingConnection = false,
                        connectionTestResult = ConnectionTestResult.Failure(e.message ?: "Unknown error")
                    )
                }
            }
        }
    }

    /**
     * Complete onboarding process.
     * Called when user clicks "Get Started" or after successful connection test.
     */
    fun completeOnboarding() {
        val currentState = _uiState.value
        if (currentState is OnboardingUiState.Ready) {
            _uiState.update { currentState.copy(isSuccess = true) }
        }
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        val currentState = _uiState.value
        if (currentState is OnboardingUiState.Ready) {
            _uiState.update { currentState.copy(error = null) }
        }
    }
}
