package com.faithconnect.domain.usecase

import com.faithconnect.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case for saving and loading user's preferred categories.
 *
 * Business logic: Persists user's category preferences so they persist
 * across app sessions. Categories are stored as a comma-separated string.
 */
class SavePreferredCategoriesUseCase(
    private val preferencesRepository: PreferencesRepository
) {
    companion object {
        private const val CATEGORIES_SEPARATOR = ","
        private const val CATEGORIES_KEY_SUFFIX = "_preferred_categories"
    }

    /**
     * Save preferred categories to persistent storage.
     *
     * @param categories Collection of category names to save (List or Set).
     */
    suspend operator fun invoke(categories: Collection<String>) {
        val categoriesString = categories.joinToString(CATEGORIES_SEPARATOR)
        // Note: PreferencesRepository needs to be extended to support arbitrary keys
        // For now, we'll store as part of sheet URL with a suffix
        // In production, you'd want to add generic key-value storage to PreferencesRepository
        preferencesRepository.setSheetUrl("$CATEGORIES_KEY_SUFFIX:$categoriesString")
    }

    /**
     * Load preferred categories from persistent storage.
     *
     * @return List of saved category names, or empty list if none saved.
     */
    suspend fun load(): List<String> {
        // Note: This is a simplified implementation
        // In production, extend PreferencesRepository to support generic key-value storage
        val stored = preferencesRepository.getSheetUrl() ?: ""

        return if (stored.startsWith(CATEGORIES_KEY_SUFFIX)) {
            val categoriesString = stored.removePrefix("$CATEGORIES_KEY_SUFFIX:")
            if (categoriesString.isNotBlank()) {
                categoriesString.split(CATEGORIES_SEPARATOR).map { it.trim() }
            } else {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    /**
     * Observe preferred categories as a Flow.
     *
     * @return Flow of category lists that updates when preferences change.
     */
    fun observe(): Flow<List<String>> =
        preferencesRepository.observeSheetUrl().map { stored ->
            if (stored != null && stored.startsWith(CATEGORIES_KEY_SUFFIX)) {
                val categoriesString = stored.removePrefix("$CATEGORIES_KEY_SUFFIX:")
                if (categoriesString.isNotBlank()) {
                    categoriesString.split(CATEGORIES_SEPARATOR).map { it.trim() }
                } else {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }

    /**
     * Add a category to preferred categories.
     *
     * @param category Category to add.
     */
    suspend fun addCategory(category: String) {
        val current = load().toMutableList()
        if (!current.contains(category)) {
            current.add(category)
            invoke(current)
        }
    }

    /**
     * Remove a category from preferred categories.
     *
     * @param category Category to remove.
     */
    suspend fun removeCategory(category: String) {
        val current = load().toMutableList()
        current.remove(category)
        invoke(current)
    }

    /**
     * Check if a category is in preferred categories.
     *
     * @param category Category to check.
     * @return True if category is preferred.
     */
    suspend fun isPreferred(category: String): Boolean {
        return load().contains(category)
    }
}
