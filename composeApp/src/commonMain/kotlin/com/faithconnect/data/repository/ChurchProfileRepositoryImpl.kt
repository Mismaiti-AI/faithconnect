package com.faithconnect.data.repository

import com.faithconnect.core.network.ApiResult
import com.faithconnect.data.local.dao.ChurchProfileDao
import com.faithconnect.data.local.entity.toDomain
import com.faithconnect.data.local.entity.toEntity
import com.faithconnect.data.remote.GoogleSheetsService
import com.faithconnect.domain.model.ChurchProfile
import com.faithconnect.domain.repository.ChurchProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Implementation of ChurchProfileRepository using Google Sheets as remote source
 * and Room database for offline caching.
 */
class ChurchProfileRepositoryImpl(
    private val googleSheetsService: GoogleSheetsService,
    private val churchProfileDao: ChurchProfileDao,
    private val sheetUrlProvider: () -> String
) : ChurchProfileRepository {

    // Private mutable state
    private val _profile = MutableStateFlow<ChurchProfile?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    // Public immutable state
    override val profile: StateFlow<ChurchProfile?> = _profile.asStateFlow()
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    override val error: StateFlow<String?> = _error.asStateFlow()

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        repositoryScope.launch {
            churchProfileDao.observe().collect { entity ->
                _profile.value = entity?.toDomain()
            }
        }
    }

    override suspend fun loadProfile(): ApiResult<Unit> {
        if (_profile.value != null) {
            return ApiResult.Success(Unit)
        }
        return refreshProfile()
    }

    override suspend fun refreshProfile(): ApiResult<Unit> {
        _isLoading.value = true
        _error.value = null

        return try {
            val sheetUrl = sheetUrlProvider()
            if (sheetUrl.isBlank()) {
                _error.value = "Google Sheets URL not configured"
                _isLoading.value = false
                return ApiResult.Error(Exception("Sheet URL not configured"))
            }

            val profile = googleSheetsService.fetchChurchProfile(sheetUrl)
            if (profile != null) {
                churchProfileDao.replace(profile.toEntity())
            }

            _isLoading.value = false
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false

            val cached = churchProfileDao.get()
            if (cached != null) {
                _profile.value = cached.toDomain()
                _error.value = "Using cached data. ${e.message}"
                ApiResult.Success(Unit)
            } else {
                _error.value = e.message ?: "Failed to load church profile"
                ApiResult.Error(e)
            }
        }
    }

    override suspend fun updateProfile(profile: ChurchProfile): ApiResult<Unit> {
        return try {
            churchProfileDao.replace(profile.toEntity())
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    override fun clearError() {
        _error.value = null
    }
}
