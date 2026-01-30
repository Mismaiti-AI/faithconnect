package com.faithconnect.di

import com.faithconnect.data.repository.ChurchProfileRepositoryImpl
import com.faithconnect.data.repository.EventRepositoryImpl
import com.faithconnect.data.repository.NewsItemRepositoryImpl
import com.faithconnect.data.repository.PreferencesRepositoryImpl
import com.faithconnect.domain.repository.ChurchProfileRepository
import com.faithconnect.domain.repository.EventRepository
import com.faithconnect.domain.repository.NewsItemRepository
import com.faithconnect.domain.repository.PreferencesRepository
import com.faithconnect.domain.usecase.ApplyCategoryFilterUseCase
import com.faithconnect.domain.usecase.FetchEventLocationUseCase
import com.faithconnect.domain.usecase.FilterEventsByCategoryUseCase
import com.faithconnect.domain.usecase.GetEventDescriptionUseCase
import com.faithconnect.domain.usecase.GetLatestNewsUseCase
import com.faithconnect.domain.usecase.GetUpcomingEventsUseCase
import com.faithconnect.domain.usecase.LoadNewsByDateRangeUseCase
import com.faithconnect.domain.usecase.MarkNewsAsReadUseCase
import com.faithconnect.domain.usecase.OpenMapForLocationUseCase
import com.faithconnect.domain.usecase.ResetFiltersUseCase
import com.faithconnect.domain.usecase.SavePreferredCategoriesUseCase
import com.faithconnect.domain.usecase.SearchEventsUseCase
import com.faithconnect.domain.usecase.SetGoogleSheetUrlUseCase
import com.faithconnect.domain.usecase.TestSheetConnectionUseCase
import com.faithconnect.domain.usecase.UpdateChurchProfileUseCase
import com.faithconnect.presentation.adminconfig.AdminConfigViewModel
import com.faithconnect.presentation.churchprofile.ChurchProfileViewModel
import com.faithconnect.presentation.eventcalendar.EventCalendarViewModel
import com.faithconnect.presentation.eventcategoryfilter.EventCategoryFilterViewModel
import com.faithconnect.presentation.eventdetail.EventDetailViewModel
import com.faithconnect.presentation.newsdetail.NewsDetailViewModel
import com.faithconnect.presentation.newsfeed.NewsFeedViewModel
import com.faithconnect.presentation.onboarding.OnboardingViewModel
import com.faithconnect.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Main application module for FaithConnect.
 *
 * Registers:
 * - Repositories (as singletons for shared state)
 * - Use Cases (as factories, stateless)
 * - ViewModels (using viewModelOf)
 */
val appModule = module {
    // ========================================
    // REPOSITORIES (Singletons)
    // ========================================
    // Shared state lives in repositories, not ViewModels

    single<PreferencesRepository> {
        PreferencesRepositoryImpl(
            preferencesDataSource = get()
        )
    }

    single<EventRepository> {
        EventRepositoryImpl(
            googleSheetsService = get(),
            eventDao = get(),
            sheetUrlProvider = {
                // Provide sheet URL from preferences
                // Note: This is a lambda to avoid initialization order issues
                runCatching {
                    kotlinx.coroutines.runBlocking {
                        get<PreferencesRepository>().getSheetUrl() ?: ""
                    }
                }.getOrDefault("")
            }
        )
    }

    single<NewsItemRepository> {
        NewsItemRepositoryImpl(
            googleSheetsService = get(),
            newsItemDao = get(),
            sheetUrlProvider = {
                runCatching {
                    kotlinx.coroutines.runBlocking {
                        get<PreferencesRepository>().getSheetUrl() ?: ""
                    }
                }.getOrDefault("")
            }
        )
    }

    single<ChurchProfileRepository> {
        ChurchProfileRepositoryImpl(
            googleSheetsService = get(),
            churchProfileDao = get(),
            sheetUrlProvider = {
                runCatching {
                    kotlinx.coroutines.runBlocking {
                        get<PreferencesRepository>().getSheetUrl() ?: ""
                    }
                }.getOrDefault("")
            }
        )
    }

    // ========================================
    // USE CASES (Factories)
    // ========================================
    // Use cases are stateless, create new instance each time

    // Event Calendar Use Cases
    factoryOf(::GetUpcomingEventsUseCase)
    factoryOf(::FilterEventsByCategoryUseCase)
    factoryOf(::SearchEventsUseCase)

    // News Feed Use Cases
    factoryOf(::GetLatestNewsUseCase)
    factoryOf(::MarkNewsAsReadUseCase)
    factoryOf(::LoadNewsByDateRangeUseCase)

    // Event Details Use Cases
    factoryOf(::FetchEventLocationUseCase)
    factoryOf(::GetEventDescriptionUseCase)
    factoryOf(::OpenMapForLocationUseCase)

    // Category Filtering Use Cases
    factoryOf(::ApplyCategoryFilterUseCase)
    factoryOf(::SavePreferredCategoriesUseCase)
    factoryOf(::ResetFiltersUseCase)

    // Admin Configuration Use Cases
    factoryOf(::SetGoogleSheetUrlUseCase)
    factoryOf(::TestSheetConnectionUseCase)
    factoryOf(::UpdateChurchProfileUseCase)

    // ========================================
    // VIEW MODELS
    // ========================================

    // Simple ViewModels (no parameters)
    viewModelOf(::AdminConfigViewModel)
    viewModelOf(::ChurchProfileViewModel)
    viewModelOf(::EventCalendarViewModel)
    viewModelOf(::EventCategoryFilterViewModel)
    viewModelOf(::NewsFeedViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::SettingsViewModel)

    // ViewModels with parameters
    // EventDetailViewModel requires eventId parameter
    factory { (eventId: String) ->
        EventDetailViewModel(
            eventId = eventId,
            repository = get(),
            fetchEventLocationUseCase = get(),
            getEventDescriptionUseCase = get(),
            openMapForLocationUseCase = get()
        )
    }

    // NewsDetailViewModel requires newsId parameter
    factory { (newsId: String) ->
        NewsDetailViewModel(
            newsId = newsId,
            repository = get(),
            markNewsAsReadUseCase = get()
        )
    }
}
