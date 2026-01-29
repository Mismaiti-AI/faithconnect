package com.faithconnect.domain.usecase

import com.faithconnect.core.network.ApiResult
import com.faithconnect.domain.model.ChurchProfile
import com.faithconnect.domain.repository.ChurchProfileRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * Use case for updating church profile information.
 *
 * Business logic: Updates church profile data and handles validation.
 * Note: For Google Sheets backend, this typically triggers a refresh to fetch
 * updated data from the sheet (since sheets are the source of truth).
 */
class UpdateChurchProfileUseCase(
    private val repository: ChurchProfileRepository
) {
    // Expose repository state (observe, don't copy!)
    val profile: StateFlow<ChurchProfile?> = repository.profile
    val isLoading: StateFlow<Boolean> = repository.isLoading
    val error: StateFlow<String?> = repository.error

    /**
     * Update church profile.
     *
     * Business logic: For Google Sheets backend, this triggers a refresh
     * to fetch the latest data from the sheet.
     *
     * @param profile Updated church profile data.
     * @return ApiResult indicating success or failure.
     */
    suspend operator fun invoke(profile: ChurchProfile): ApiResult<Unit> {
        // Validate profile
        val validationError = validateProfile(profile)
        if (validationError != null) {
            return ApiResult.Error(validationError)
        }

        return repository.updateProfile(profile)
    }

    /**
     * Refresh profile from Google Sheets.
     *
     * Business logic: Fetches the latest profile data from the configured sheet.
     */
    suspend fun refresh(): ApiResult<Unit> {
        return repository.refreshProfile()
    }

    /**
     * Load profile from local cache or remote.
     */
    suspend fun load(): ApiResult<Unit> {
        return repository.loadProfile()
    }

    /**
     * Validate church profile data.
     *
     * Business logic: Ensures required fields are present and valid.
     *
     * @param profile Profile to validate.
     * @return Error message if invalid, null if valid.
     */
    fun validateProfile(profile: ChurchProfile): String? {
        // Check required fields
        if (profile.name.isBlank()) {
            return "Church name is required"
        }

        // Validate email format if provided
        if (profile.email.isNotBlank() && !isValidEmail(profile.email)) {
            return "Invalid email format"
        }

        // Validate website URL if provided
        if (profile.website.isNotBlank() && !isValidUrl(profile.website)) {
            return "Invalid website URL format"
        }

        // Validate phone format if provided (basic check)
        if (profile.phone.isNotBlank() && !isValidPhone(profile.phone)) {
            return "Invalid phone number format"
        }

        return null // Valid
    }

    /**
     * Check if profile has required information.
     *
     * @param profile Profile to check.
     * @return True if all required fields are filled.
     */
    fun isComplete(profile: ChurchProfile?): Boolean {
        if (profile == null) return false
        return profile.name.isNotBlank() &&
               profile.address.isNotBlank() &&
               profile.phone.isNotBlank()
    }

    /**
     * Get profile completeness percentage.
     *
     * @param profile Profile to check.
     * @return Completeness percentage (0-100).
     */
    fun getCompletenessPercentage(profile: ChurchProfile?): Int {
        if (profile == null) return 0

        val fields = listOf(
            profile.name,
            profile.logoURL,
            profile.welcomeMessage,
            profile.address,
            profile.phone,
            profile.website,
            profile.email,
            profile.mission,
            profile.serviceTimes,
            profile.socialFacebook
        )

        val filledFields = fields.count { it.isNotBlank() }
        return (filledFields * 100) / fields.size
    }

    // Validation helpers

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    private fun isValidUrl(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://")
    }

    private fun isValidPhone(phone: String): Boolean {
        // Basic validation: must contain at least some digits
        return phone.any { it.isDigit() }
    }
}
