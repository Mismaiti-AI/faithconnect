package com.faithconnect.presentation.churchprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithconnect.core.network.ApiResult
import com.faithconnect.domain.model.ChurchProfile
import com.faithconnect.domain.repository.ChurchProfileRepository
import com.faithconnect.domain.usecase.UpdateChurchProfileUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * THIN ViewModel for Church Profile screen.
 *
 * Observes repository state and delegates business logic to use cases.
 */
class ChurchProfileViewModel(
    private val repository: ChurchProfileRepository,
    private val updateChurchProfileUseCase: UpdateChurchProfileUseCase
) : ViewModel() {

    // Observe repository state (don't copy!)
    val uiState: StateFlow<ChurchProfileUiState> = combine(
        repository.profile,
        repository.isLoading,
        repository.error
    ) { profile, isLoading, error ->
        when {
            error != null -> ChurchProfileUiState.Error(error)
            profile != null || !isLoading -> ChurchProfileUiState.Success(
                profile = profile,
                isRefreshing = isLoading,
                error = null
            )
            else -> ChurchProfileUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChurchProfileUiState.Loading
    )

    init {
        loadProfile()
    }

    /**
     * Load church profile from repository.
     */
    fun loadProfile() {
        viewModelScope.launch {
            repository.loadProfile()
        }
    }

    /**
     * Refresh church profile from remote source.
     */
    fun refresh() {
        viewModelScope.launch {
            repository.refreshProfile()
        }
    }

    /**
     * Update church profile with new data.
     *
     * @param profile The updated church profile.
     */
    fun updateProfile(profile: ChurchProfile) {
        viewModelScope.launch {
            when (val result = updateChurchProfileUseCase(profile)) {
                is ApiResult.Success -> {
                    // Profile updated successfully
                    repository.loadProfile()
                }
                is ApiResult.Error -> {
                    // Error handled by repository state
                }
            }
        }
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        repository.clearError()
    }
}
