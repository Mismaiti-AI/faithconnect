package com.faithconnect.domain.repository

import com.faithconnect.core.network.ApiResult
import com.faithconnect.domain.model.ChurchProfile
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for ChurchProfile data operations.
 *
 * Provides shared state management for church profile across multiple screens.
 * All ViewModels observe the same StateFlow instance from this repository.
 */
interface ChurchProfileRepository {
    // Shared state - observable by all ViewModels
    val profile: StateFlow<ChurchProfile?>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>

    // Actions - called by ViewModels/UseCases
    suspend fun loadProfile(): ApiResult<Unit>
    suspend fun refreshProfile(): ApiResult<Unit>
    suspend fun updateProfile(profile: ChurchProfile): ApiResult<Unit>
    fun clearError()
}
