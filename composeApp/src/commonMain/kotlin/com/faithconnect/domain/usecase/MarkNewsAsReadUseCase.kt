package com.faithconnect.domain.usecase

import com.faithconnect.domain.repository.NewsItemRepository

/**
 * Use case for marking news items as read.
 *
 * Business logic: Tracks which news items have been read by the user.
 * Note: This implementation selects the news item in the repository,
 * which indicates the user has viewed it. For a full read/unread system,
 * you would need to add a 'isRead' field to the NewsItem model and
 * persist it in the database.
 */
class MarkNewsAsReadUseCase(
    private val repository: NewsItemRepository
) {
    /**
     * Mark a news item as read by selecting it.
     * This indicates the user has viewed the item.
     *
     * @param newsId The ID of the news item to mark as read.
     */
    suspend operator fun invoke(newsId: String) {
        // Select the news item (indicates it has been viewed)
        repository.selectNewsItem(newsId)
    }

    /**
     * Clear the current selection.
     * Used when navigating away from a news detail screen.
     */
    fun clearSelection() {
        repository.clearSelection()
    }
}
