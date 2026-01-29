# Implementation Plan: FaithConnect

> Auto-generated checklist from project-context.json
>
> **Purpose:** Help congregation get an update of church activities, special events like seminar or workshop, news and article
>
> **Backend:** google_sheets

---

## 📋 How to Use This Plan

**This plan is a COMPLETENESS CHECKLIST**, not a specification document.

1. **For detailed specifications** (entity fields, feature descriptions, UI colors, etc.):
   → Read `project-context.json`

2. **For development sequence** (what to build and when):
   → Follow this implementation plan phase-by-phase

3. **For implementation patterns** (how to write the code):
   → Load the specified skills using `Skill(skill="skill-name")`

4. **Mark tasks as complete** (as you work):
   → Change `- [ ]` to `- [x]` after completing each task
   → This provides real-time progress tracking

**Goal:** Ensure ZERO empty folders, classes, or functions. Every checkbox must be complete!

---

## Prerequisites - MUST READ FIRST

**Before starting any phase, review these pattern guidelines:**

1. **Platform-Specific Code:** Use Interface + platformModule injection pattern (NOT expect object)
2. **External Data:** Support multiple input formats, handle missing data gracefully
3. **State Management:** Check existing state before showing input screens

See `CLAUDE.md` (Critical Code Rules section) for detailed patterns.

**For Google Sheets backend:** Load `gsheet-skill` for multi-tab GID discovery and CSV validation patterns.

---

## Overview

This implementation plan outlines the development phases for FaithConnect.
Each phase specifies which skills to load to minimize token usage.

**Total Phases:** 9
**Entities:** 3
**Screens:** 6

---

## Phase 1: Theme

**Load these skills using the Skill tool:**
- `Skill(skill="theme-skill")`

**Description:** Update existing theme files with ui_design colors

**Tasks:**
- [x] **Update existing theme files (already in template):**
- [x]   - `AppColors.kt` - Update color values from ui_design
- [x]   - `AppTheme.kt` - Update MaterialTheme with new colors
- [x]   - `AppTheme.android.kt` - Platform-specific (already exists)
- [x]   - `AppTheme.ios.kt` - Platform-specific (already exists)
- [x] Apply colors from project-context.json ui_design section
- [x] Ensure dark color scheme is properly configured

---

## Phase 2: Domain Models

**Load these skills using the Skill tool:**
- `Skill(skill="data-skill")`

**Description:** Create domain model data classes (specs from project-context.json)

**Tasks:**
- [x] **📖 Read entity specifications from `project-context.json → data_models`**
- [x]
- [x] **Create Domain Models in `domain/model/`:**
- [x]   - [x] `Event.kt` (11 fields - see context.json)
- [x]   - [x] `NewsItem.kt` (10 fields - see context.json)
- [x]   - [x] `ChurchProfile.kt` (10 fields - see context.json)
- [x]
- [x] **Implementation Rules:**
- [x]   - Use `kotlin.time.Instant` for timestamps (NOT kotlinx.datetime)
- [x]   - Make all models `data class` with sensible defaults
- [x]   - Each model must have ALL fields from project-context.json
- [x]
- [x] **✅ Completeness Check:**
- [x]   - Every entity in context.json has a .kt file
- [x]   - Each model has ALL required fields (no missing properties)
- [x]   - No empty data classes or TODOs

---

## Phase 3: Data Layer

**Load these skills using the Skill tool:**
- `Skill(skill="data-skill")`
- `Skill(skill="gsheet-skill")`
- `Skill(skill="database-skill")`

**Description:** Create services, database, and repositories with state management

**Tasks:**
- [x] **Create Google Sheets Service:**
- [x]   - `data/remote/GoogleSheetsService.kt` - CSV fetching with GID discovery
- [x]   - Support both edit URLs and published URLs (pubhtml)
- [x]   - Implement multi-format date parsing (yyyy-MM-dd, yyyy/MM/dd, dd/MM/yyyy)
- [x]   - Add CSV response validation (check for HTML error pages)
- [x]   - Handle missing tabs gracefully (return empty list, not crash)
- [x] **Create Room Database:**
- [x]   - `core/database/AppDatabase.kt` - Room @Database
- [x]   - `data/local/entity/EventEntity.kt` - Room @Entity
- [x]   - `data/local/dao/EventDao.kt` - Room @Dao with CRUD
- [x]   - `data/local/entity/NewsItemEntity.kt` - Room @Entity
- [x]   - `data/local/dao/NewsItemDao.kt` - Room @Dao with CRUD
- [x]   - `data/local/entity/ChurchProfileEntity.kt` - Room @Entity
- [x]   - `data/local/dao/ChurchProfileDao.kt` - Room @Dao with CRUD
- [x]   - Store timestamps as Long (not Instant) in entities
- [x] **Create Repositories (with STATE MANAGEMENT):**
- [x]   - [x] `domain/repository/ChurchProfileRepository.kt` (interface)
- [x]   - [x] `data/repository/churchprofile/ChurchProfileRepositoryImpl.kt` (implementation)
- [x]   - [x] `domain/repository/EventRepository.kt` (interface)
- [x]   - [x] `data/repository/event/EventRepositoryImpl.kt` (implementation)
- [x]   - [x] `domain/repository/NewsItemRepository.kt` (interface)
- [x]   - [x] `data/repository/newsitem/NewsItemRepositoryImpl.kt` (implementation)
- [x]   - [x] `domain/repository/PreferencesRepository.kt` (interface)
- [x]   - [x] `data/repository/preferences/PreferencesRepositoryImpl.kt` (implementation)
- [x]   - Each repository holds `MutableStateFlow` for its data
- [x]   - Exposes `StateFlow` to ViewModels (read-only)
- [x]   - Implements offline-first: cache on fetch, serve from cache when offline

---

## Phase 4: Use Cases

**Load these skills using the Skill tool:**
- `Skill(skill="data-skill")`

**Description:** Create use case classes for business logic

**Tasks:**
- [x] **Create Use Cases in `domain/usecase/`:**
- [x]   - `GetUpcomingEventsUseCase.kt` (feature: Event Calendar)
- [x]   - `FilterEventsByCategoryUseCase.kt` (feature: Event Calendar)
- [x]   - `SearchEventsUseCase.kt` (feature: Event Calendar)
- [x]   - `GetLatestNewsUseCase.kt` (feature: News Feed)
- [x]   - `MarkNewsAsReadUseCase.kt` (feature: News Feed)
- [x]   - `LoadNewsByDateRangeUseCase.kt` (feature: News Feed)
- [x]   - `FetchEventLocationUseCase.kt` (feature: Event Details)
- [x]   - `GetEventDescriptionUseCase.kt` (feature: Event Details)
- [x]   - `OpenMapForLocationUseCase.kt` (feature: Event Details)
- [x]   - `ApplyCategoryFilterUseCase.kt` (feature: Category Filtering)
- [x]   - `SavePreferredCategoriesUseCase.kt` (feature: Category Filtering)
- [x]   - `ResetFiltersUseCase.kt` (feature: Category Filtering)
- [x]   - `SetGoogleSheetUrlUseCase.kt` (feature: Admin Configuration)
- [x]   - `TestSheetConnectionUseCase.kt` (feature: Admin Configuration)
- [x]   - `UpdateChurchProfileUseCase.kt` (feature: Admin Configuration)
- [x] Each UseCase: single `operator fun invoke()` or `suspend operator fun invoke()`
- [x] UseCase calls Repository, applies business logic, returns result
- [x] Keep UseCases focused - one responsibility each

---

## Phase 5: ViewModels

**Load these skills using the Skill tool:**
- `Skill(skill="feature-orchestration-skill")`
- `Skill(skill="coroutine-flow-skill")`

**Description:** Create THIN ViewModels that observe repository state

**Tasks:**
- [x] **Create ViewModels (feature-based organization):**
- [x]   - [x] `presentation/adminconfig/AdminConfigViewModel.kt` with `AdminConfigUiState` sealed interface
- [x]   - [x] `presentation/churchprofile/ChurchProfileViewModel.kt` with `ChurchProfileUiState` sealed interface
- [x]   - [x] `presentation/eventcalendar/EventCalendarViewModel.kt` with `EventCalendarUiState` sealed interface
- [x]   - [x] `presentation/eventcategoryfilter/EventCategoryFilterViewModel.kt` with `EventCategoryFilterUiState` sealed interface
- [x]   - [x] `presentation/eventdetail/EventDetailViewModel.kt` with `EventDetailUiState` sealed interface
- [x]   - [x] `presentation/newsdetail/NewsDetailViewModel.kt` with `NewsDetailUiState` sealed interface
- [x]   - [x] `presentation/newsfeed/NewsFeedViewModel.kt` with `NewsFeedUiState` sealed interface
- [x]   - [x] `presentation/onboarding/OnboardingViewModel.kt` with `OnboardingUiState` sealed interface
- [x]   - [x] `presentation/settings/SettingsViewModel.kt` with `SettingsUiState` sealed interface
- [x] **UiState Pattern:**
- [x]   - `sealed interface XxxUiState { Loading, Success(data), Error(message) }`
- [x]   - ViewModel exposes `val uiState: StateFlow<XxxUiState>`
- [x] **THIN ViewModel Pattern:**
- [x]   - Observe repository StateFlow, transform to UiState
- [x]   - NO business logic in ViewModel - delegate to UseCases
- [x]   - Use `viewModelScope.launch` for coroutines

---

## Phase 6: Screens

**Load these skills using the Skill tool:**
- `Skill(skill="ui-skill")`

**Description:** Create screen composables with proper state handling

**Tasks:**
- [x] **Create Screens (feature-based organization):**
- [x]   - [x] `presentation/adminconfig/AdminConfigScreen.kt` using `AdminConfigViewModel`
- [x]   - [x] `presentation/adminconfig/AdminConfigUiState.kt` sealed interface
- [x]   - [x] `presentation/churchprofile/ChurchProfileScreen.kt` using `ChurchProfileViewModel`
- [x]   - [x] `presentation/churchprofile/ChurchProfileUiState.kt` sealed interface
- [x]   - [x] `presentation/eventcalendar/EventCalendarScreen.kt` using `EventCalendarViewModel`
- [x]   - [x] `presentation/eventcalendar/EventCalendarUiState.kt` sealed interface
- [x]   - [x] `presentation/eventcategoryfilter/EventCategoryFilterScreen.kt` using `EventCategoryFilterViewModel`
- [x]   - [x] `presentation/eventcategoryfilter/EventCategoryFilterUiState.kt` sealed interface
- [x]   - [x] `presentation/eventdetail/EventDetailScreen.kt` using `EventDetailViewModel`
- [x]   - [x] `presentation/eventdetail/EventDetailUiState.kt` sealed interface
- [x]   - [x] `presentation/newsdetail/NewsDetailScreen.kt` using `NewsDetailViewModel`
- [x]   - [x] `presentation/newsdetail/NewsDetailUiState.kt` sealed interface
- [x]   - [x] `presentation/newsfeed/NewsFeedScreen.kt` using `NewsFeedViewModel`
- [x]   - [x] `presentation/newsfeed/NewsFeedUiState.kt` sealed interface
- [x]   - [x] `presentation/onboarding/OnboardingScreen.kt` using `OnboardingViewModel`
- [x]   - [x] `presentation/onboarding/OnboardingUiState.kt` sealed interface
- [x]   - [x] `presentation/settings/SettingsScreen.kt` using `SettingsViewModel`
- [x]   - [x] `presentation/settings/SettingsUiState.kt` sealed interface
- [x] **Screen Pattern:**
- [x]   - `@Composable fun XxxScreen(viewModel: XxxViewModel = koinViewModel())`
- [x]   - Collect state: `val uiState by viewModel.uiState.collectAsState()`
- [x]   - Handle Loading/Success/Error states with `when(uiState)`
- [x]   - Use `koinViewModel()` as DEFAULT parameter (never pass null)
- [x] **Common Components:**
- [x]   - [x] `presentation/components/ErrorView.kt` - Error display with retry
- [x]   - [x] `presentation/components/LoadingView.kt` - Loading indicator
- [x]   - [x] `presentation/components/EmptyView.kt` - Empty state display

---

## Phase 7: Navigation

**Load these skills using the Skill tool:**
- `Skill(skill="ui-skill")`

**Description:** Set up type-safe navigation with @Serializable routes

**Tasks:**
- [x] **Create Navigation in `navigation/`:**
- [x]   - `NavRoutes.kt` - @Serializable route classes (NOT string routes)
- [x]   - `NavigationHost.kt` - NavHost with composable<Route> entries
- [x]   - Start destination: `HomeScreen`
- [x] **Navigation Flows:**
- [x]   - HomeScreen -> NewsFeedScreen (tap 'Latest News')
- [x]   - HomeScreen -> EventCalendarScreen (tap 'Upcoming Events')
- [x]   - HomeScreen -> ChurchProfileScreen (tap 'About Us')
- [x]   - ChurchProfileScreen -> SetupScreen (tap 'Update Sheet')
- [x]   - SetupScreen -> HomeScreen (after successful sheet load)
- [x]   - ... (+1 more flows)
- [x] **Type-Safe Navigation:**
- [x]   - `@Serializable object Home` for simple routes
- [x]   - `@Serializable data class Detail(val id: String)` for parameterized routes
- [x]   - Navigate: `navController.navigate(Detail(id = "123"))`
- [x] For setup screens: check existing state in ViewModel init (skip if data exists)

---

## Phase 8: Dependency Injection

**Load these skills using the Skill tool:**
- `Skill(skill="koin-di-skill")`

**Description:** Register all classes in Koin (now that they all exist)

**Tasks:**
- [x] **Create Koin Modules in `di/`:**
- [x]   - `AppModule.kt` - main module with all registrations
- [x]   - `NetworkModule.kt` - HttpClient and GoogleSheetsService
- [x]   - `DatabaseModule.kt` - Room database and DAOs
- [x]   - `PlatformModule.kt` - expect fun platformModule(): Module in commonMain
- [x]   - `PlatformModule.android.kt` - actual fun platformModule() in androidMain
- [x]   - `PlatformModule.ios.kt` - actual fun platformModule() in iosMain
- [x]
- [x] **Register ALL classes created in previous phases:**
- [x] *Repositories:*
- [x]   - `single<PreferencesRepository> { PreferencesRepositoryImpl(...) }`
- [x]   - `single<EventRepository> { EventRepositoryImpl(...) }`
- [x]   - `single<NewsItemRepository> { NewsItemRepositoryImpl(...) }`
- [x]   - `single<ChurchProfileRepository> { ChurchProfileRepositoryImpl(...) }`
- [x] *UseCases:*
- [x]   - `factoryOf(::GetUpcomingEventsUseCase)`
- [x]   - `factoryOf(::FilterEventsByCategoryUseCase)`
- [x]   - `factoryOf(::SearchEventsUseCase)`
- [x]   - `factoryOf(::GetLatestNewsUseCase)`
- [x]   - `factoryOf(::MarkNewsAsReadUseCase)`
- [x]   - `factoryOf(::LoadNewsByDateRangeUseCase)`
- [x]   - `factoryOf(::FetchEventLocationUseCase)`
- [x]   - `factoryOf(::GetEventDescriptionUseCase)`
- [x]   - `factoryOf(::OpenMapForLocationUseCase)`
- [x]   - `factoryOf(::ApplyCategoryFilterUseCase)`
- [x]   - `factoryOf(::SavePreferredCategoriesUseCase)`
- [x]   - `factoryOf(::ResetFiltersUseCase)`
- [x]   - `factoryOf(::SetGoogleSheetUrlUseCase)`
- [x]   - `factoryOf(::TestSheetConnectionUseCase)`
- [x]   - `factoryOf(::UpdateChurchProfileUseCase)`
- [x] *ViewModels:*
- [x]   - `viewModelOf(::AdminConfigViewModel)`
- [x]   - `viewModelOf(::ChurchProfileViewModel)`
- [x]   - `viewModelOf(::EventCalendarViewModel)`
- [x]   - `viewModelOf(::EventCategoryFilterViewModel)`
- [x]   - `factory { (eventId: String) -> EventDetailViewModel(...) }` (with parameter)
- [x]   - `factory { (newsId: String) -> NewsDetailViewModel(...) }` (with parameter)
- [x]   - `viewModelOf(::NewsFeedViewModel)`
- [x]   - `viewModelOf(::OnboardingViewModel)`
- [x]   - `viewModelOf(::SettingsViewModel)`
- [x]
- [x] **Platform-Specific Dependencies:**
- [x]   - AppDatabase registered in platformModule (Android: with Context, iOS: with file path)
- [x]   - PreferencesDataSource registered in platformModule (Android: DataStore, iOS: NSUserDefaults)
- [x]   - Updated App.kt to initialize Koin with all modules

---

## Phase 9: Review & Fix

**Load these skills using the Skill tool:**
- `Skill(skill="validation-skill")`

**Description:** Review all phases and fix potential issues before GitHub Actions build

**Tasks:**
- [x] **Review Each Phase for Common Issues:**
- [x]
- [x] *Theme:*
- [x]   - Colors match ui_design specifications
- [x]   - Dark mode properly toggles if enabled
- [x]
- [x] *Domain Models:*
- [x]   - Using `kotlin.time.Instant` (NOT kotlinx.datetime)
- [x]   - All fields have sensible defaults
- [x]
- [x] *Data Layer:*
- [x]   - Repositories hold StateFlow (state management)
- [x]   - Timestamps stored as Long in Room entities
- [x]   - Error handling returns empty list, not crash
- [x]
- [x] *ViewModels:*
- [x]   - THIN pattern - observe repository, no business logic
- [x]   - UiState sealed interface with Loading/Success/Error
- [x]
- [x] *Screens:*
- [x]   - `koinViewModel()` as default parameter
- [x]   - Handles all UiState branches
- [x]
- [x] *Navigation:*
- [x]   - @Serializable route classes (not strings)
- [x]   - Icons use `Icons.AutoMirrored.Filled` for arrows/lists
- [x]
- [x] *DI:*
- [x]   - ALL ViewModels registered with `viewModelOf()`
- [x]   - ALL Repositories registered with `singleOf()`
- [x]
- [x] **Final Tasks:**
- [x]   - Update README.md with app name, description, features from project-context.json
- [x]   - Remove any placeholder comments
- [x]   - Ensure no empty folders remain

---

## Implementation Notes

### How to Load Skills

**Use the Skill tool** to load each skill before implementing a phase:

```
Skill(skill="core-skill")
Skill(skill="theme-skill")
```

1. Call `Skill(skill="skill-name")` for each skill listed in the phase
2. The skill content will be loaded with detailed patterns and examples
3. Use the loaded patterns to complete the phase tasks
4. Move to the next phase and repeat

### Key Patterns

- **THIN ViewModels**: ViewModels should only observe repository state, not hold business logic
- **Repository State Management**: Shared state lives in repositories, not ViewModels
- **Type-Safe Navigation**: Use sealed classes for navigation routes
- **Flow Patterns**: Use StateFlow for state, Flow for one-shot operations

### Files Reference

- `docs/project-context.json` - Complete project specifications
- `docs/implementation-plan.md` - This file

---

## 🎯 Completeness Validation (NO EMPTY IMPLEMENTATIONS!)

Before marking the implementation as complete, verify:

### ✅ File-Level Completeness
- [ ] **NO empty folders** - Every folder has at least one file
- [ ] **NO empty files** - Every .kt file has implementation code
- [ ] **NO placeholder comments** - Remove all `// TODO`, `// Add implementation here`

### ✅ Class-Level Completeness
- [ ] Every `data class` has ALL fields from project-context.json (no missing properties)
- [ ] Every repository has ALL CRUD methods fully implemented (not just stubs)
- [ ] Every ViewModel has:
  - UiState sealed interface with Loading, Success, Error states
  - StateFlow exposure
  - Intent/action handlers fully implemented
- [ ] Every UseCase has complete `invoke()` implementation (not just `TODO()`)

### ✅ Function-Level Completeness
- [ ] NO functions with just `TODO()` or empty body
- [ ] All repository methods return actual data (not `emptyList()` stubs)
- [ ] All navigation functions properly navigate (not placeholder code)
- [ ] All error handlers have proper error messages

### ✅ Screen-Level Completeness
- [ ] Every screen from project-context.json has a corresponding .kt file
- [ ] Each screen has a complete Compose UI (not just `Text("TODO")`)
- [ ] Each screen properly observes ViewModel state
- [ ] Each screen has proper error/loading states

### ✅ Integration Completeness
- [ ] All ViewModels registered in Koin DI
- [ ] All Repositories registered in Koin DI
- [ ] All navigation routes defined
- [ ] All bottom nav items (if specified) present

**CRITICAL**: The goal is a COMPLETE, BUILD-PASSING application with ZERO empty implementations!

---

*Generated by Mismaiti Backend*
