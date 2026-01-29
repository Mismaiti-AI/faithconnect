package com.faithconnect.domain.usecase

import com.faithconnect.domain.model.Event
import com.faithconnect.domain.model.NewsItem
import com.faithconnect.domain.repository.EventRepository
import com.faithconnect.domain.repository.NewsItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Use case for applying category filters to events and news.
 *
 * Business logic: Filters both events and news items by selected categories.
 * Supports multi-category filtering and provides combined results.
 */
class ApplyCategoryFilterUseCase(
    private val eventRepository: EventRepository,
    private val newsRepository: NewsItemRepository
) {
    /**
     * Apply category filter to events.
     *
     * @param categories List of categories to filter by. Empty list returns all.
     * @return Flow of filtered events.
     */
    fun filterEvents(categories: List<String>): Flow<List<Event>> =
        eventRepository.events.combine(
            kotlinx.coroutines.flow.flowOf(categories)
        ) { events, cats ->
            if (cats.isEmpty()) {
                events
            } else {
                events.filter { event ->
                    cats.any { category ->
                        event.category.equals(category, ignoreCase = true)
                    }
                }
            }
        }

    /**
     * Apply category filter to news items.
     *
     * @param categories List of categories to filter by. Empty list returns all.
     * @return Flow of filtered news items.
     */
    fun filterNews(categories: List<String>): Flow<List<NewsItem>> =
        newsRepository.newsItems.combine(
            kotlinx.coroutines.flow.flowOf(categories)
        ) { news, cats ->
            if (cats.isEmpty()) {
                news
            } else {
                news.filter { item ->
                    cats.any { category ->
                        item.category.equals(category, ignoreCase = true)
                    }
                }
            }
        }

    /**
     * Get combined filtered results (events + news) for a category.
     *
     * Business logic: Useful for showing mixed content feed filtered by category.
     *
     * @param category Single category to filter by.
     * @return Flow of pair (filtered events, filtered news).
     */
    operator fun invoke(category: String): Flow<Pair<List<Event>, List<NewsItem>>> =
        eventRepository.events.combine(newsRepository.newsItems) { events, news ->
            val filteredEvents = if (category.isBlank()) {
                events
            } else {
                events.filter { it.category.equals(category, ignoreCase = true) }
            }

            val filteredNews = if (category.isBlank()) {
                news
            } else {
                news.filter { it.category.equals(category, ignoreCase = true) }
            }

            Pair(filteredEvents, filteredNews)
        }

    /**
     * Get count of items per category (for display in filter UI).
     *
     * @return Flow of map: category -> count of items.
     */
    fun getCategoryCounts(): Flow<Map<String, Int>> =
        eventRepository.events.combine(newsRepository.newsItems) { events, news ->
            val counts = mutableMapOf<String, Int>()

            // Count events by category
            events.forEach { event ->
                if (event.category.isNotBlank()) {
                    counts[event.category] = counts.getOrDefault(event.category, 0) + 1
                }
            }

            // Count news by category
            news.forEach { item ->
                if (item.category.isNotBlank()) {
                    counts[item.category] = counts.getOrDefault(item.category, 0) + 1
                }
            }

            counts
        }
}
