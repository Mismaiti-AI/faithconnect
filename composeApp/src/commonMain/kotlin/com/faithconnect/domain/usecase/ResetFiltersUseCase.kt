package com.faithconnect.domain.usecase

/**
 * Use case for resetting all applied filters.
 *
 * Business logic: Clears all active filters and returns data to unfiltered state.
 * This is a coordination use case that works with the filtering system.
 */
class ResetFiltersUseCase(
    private val savePreferredCategoriesUseCase: SavePreferredCategoriesUseCase
) {
    /**
     * Reset all filters to default (show all categories).
     *
     * Business logic: Clears saved preferred categories and returns
     * empty list to indicate "show all".
     *
     * @return Empty list representing no filters applied.
     */
    suspend operator fun invoke(): List<String> {
        // Clear saved preferred categories
        savePreferredCategoriesUseCase.invoke(emptyList())
        return emptyList()
    }

    /**
     * Get default filter state (no filters).
     *
     * @return Empty list representing no filters.
     */
    fun getDefaultFilters(): List<String> {
        return emptyList()
    }

    /**
     * Check if filters are currently active.
     *
     * @param activeCategories Current active category filters.
     * @return True if any filters are active (non-empty list).
     */
    fun hasActiveFilters(activeCategories: List<String>): Boolean {
        return activeCategories.isNotEmpty()
    }

    /**
     * Reset to specific default categories.
     *
     * Useful for resetting to a set of default categories instead of showing all.
     *
     * @param defaultCategories Default categories to reset to.
     * @return The default categories list.
     */
    suspend fun resetToDefaults(defaultCategories: List<String>): List<String> {
        savePreferredCategoriesUseCase.invoke(defaultCategories)
        return defaultCategories
    }
}
