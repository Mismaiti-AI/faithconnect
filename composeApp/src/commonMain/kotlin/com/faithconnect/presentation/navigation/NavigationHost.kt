package com.faithconnect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.faithconnect.presentation.adminconfig.AdminConfigScreen
import com.faithconnect.presentation.churchprofile.ChurchProfileScreen
import com.faithconnect.presentation.eventcalendar.EventCalendarScreen
import com.faithconnect.presentation.eventcategoryfilter.EventCategoryFilterScreen
import com.faithconnect.presentation.eventdetail.EventDetailScreen
import com.faithconnect.presentation.home.HomeScreen
import com.faithconnect.presentation.newsdetail.NewsDetailScreen
import com.faithconnect.presentation.newsfeed.NewsFeedScreen
import com.faithconnect.presentation.onboarding.OnboardingScreen
import com.faithconnect.presentation.settings.SettingsScreen

/**
 * Main navigation host for FaithConnect app
 *
 * Sets up type-safe navigation using @Serializable routes
 * Start destination: HomeScreen
 *
 * Navigation flows:
 * - HomeScreen -> NewsFeedScreen (tap 'Latest News')
 * - HomeScreen -> EventCalendarScreen (tap 'Upcoming Events')
 * - HomeScreen -> ChurchProfileScreen (tap 'About Us')
 * - ChurchProfileScreen -> OnboardingSetup (tap 'Update Sheet')
 * - OnboardingSetup -> HomeScreen (after successful sheet load)
 * - EventCalendarScreen -> EventDetailScreen (tap event card)
 * - NewsFeedScreen -> NewsDetailScreen (tap news card)
 */
@Composable
fun NavigationHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        // ═══════════════════════════════════════════════════════════════
        // HOME SCREEN - Landing page with feature cards
        // ═══════════════════════════════════════════════════════════════
        composable<Home> {
            HomeScreen(
                onNavigateToNews = { navController.navigate(NewsFeed) },
                onNavigateToEvents = { navController.navigate(EventCalendar) },
                onNavigateToProfile = { navController.navigate(ChurchProfile) },
                onNavigateToSettings = { navController.navigate(Settings) }
            )
        }

        // ═══════════════════════════════════════════════════════════════
        // ONBOARDING & SETUP
        // ═══════════════════════════════════════════════════════════════
        composable<OnboardingSetup> {
            OnboardingScreen(
                onSetupComplete = {
                    navController.navigate(Home) {
                        popUpTo(OnboardingSetup) { inclusive = true }
                    }
                }
            )
        }

        // ═══════════════════════════════════════════════════════════════
        // EVENT SCREENS
        // ═══════════════════════════════════════════════════════════════
        composable<EventCalendar> {
            EventCalendarScreen(
                onEventClick = { eventId ->
                    navController.navigate(EventDetail(eventId = eventId))
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<EventDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<EventDetail>()
            EventDetailScreen(
                eventId = route.eventId,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<EventCategoryFilter> {
            EventCategoryFilterScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // ═══════════════════════════════════════════════════════════════
        // NEWS SCREENS
        // ═══════════════════════════════════════════════════════════════
        composable<NewsFeed> {
            NewsFeedScreen(
                onNewsClick = { newsId ->
                    navController.navigate(NewsDetail(newsId = newsId))
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<NewsDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<NewsDetail>()
            NewsDetailScreen(
                newsId = route.newsId,
                onNavigateBack = { navController.navigateUp() },
                onShare = {
                    // Share functionality - platform-specific implementation needed
                }
            )
        }

        // ═══════════════════════════════════════════════════════════════
        // CHURCH PROFILE & SETTINGS
        // ═══════════════════════════════════════════════════════════════
        composable<ChurchProfile> {
            ChurchProfileScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToSetup = { navController.navigate(OnboardingSetup) }
            )
        }

        composable<Settings> {
            SettingsScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToConfig = { navController.navigate(AdminConfig) }
            )
        }

        composable<AdminConfig> {
            AdminConfigScreen(
                onNavigateBack = { navController.navigateUp() },
                onConfigSaved = { navController.navigateUp() }
            )
        }
    }
}
