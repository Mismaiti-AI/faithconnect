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
- [ ] **Update existing theme files (already in template):**
- [ ]   - `AppColors.kt` - Update color values from ui_design
- [ ]   - `AppTheme.kt` - Update MaterialTheme with new colors
- [ ]   - `AppTheme.android.kt` - Platform-specific (already exists)
- [ ]   - `AppTheme.ios.kt` - Platform-specific (already exists)
- [ ] Apply colors from project-context.json ui_design section
- [ ] Ensure dark color scheme is properly configured

---

## Phase 2: Domain Models

**Load these skills using the Skill tool:**
- `Skill(skill="data-skill")`

**Description:** Create domain model data classes (specs from project-context.json)

**Tasks:**
- [ ] **📖 Read entity specifications from `project-context.json → data_models`**
- [ ] 
- [ ] **Create Domain Models in `domain/model/`:**
- [ ]   - [ ] `Event.kt` (11 fields - see context.json)
- [ ]   - [ ] `NewsItem.kt` (10 fields - see context.json)
- [ ]   - [ ] `ChurchProfile.kt` (10 fields - see context.json)
- [ ] 
- [ ] **Implementation Rules:**
- [ ]   - Use `kotlin.time.Instant` for timestamps (NOT kotlinx.datetime)
- [ ]   - Make all models `data class` with sensible defaults
- [ ]   - Each model must have ALL fields from project-context.json
- [ ] 
- [ ] **✅ Completeness Check:**
- [ ]   - Every entity in context.json has a .kt file
- [ ]   - Each model has ALL required fields (no missing properties)
- [ ]   - No empty data classes or TODOs

---

## Phase 3: Data Layer

**Load these skills using the Skill tool:**
- `Skill(skill="data-skill")`
- `Skill(skill="gsheet-skill")`
- `Skill(skill="database-skill")`

**Description:** Create services, database, and repositories with state management

**Tasks:**
- [ ] **Create Google Sheets Service:**
- [ ]   - `data/remote/GoogleSheetsService.kt` - CSV fetching with GID discovery
- [ ]   - Support both edit URLs and published URLs (pubhtml)
- [ ]   - Implement multi-format date parsing (yyyy-MM-dd, yyyy/MM/dd, dd/MM/yyyy)
- [ ]   - Add CSV response validation (check for HTML error pages)
- [ ]   - Handle missing tabs gracefully (return empty list, not crash)
- [ ] **Create Room Database:**
- [ ]   - `data/local/AppDatabase.kt` - Room @Database
- [ ]   - `data/local/entity/EventEntity.kt` - Room @Entity
- [ ]   - `data/local/dao/EventDao.kt` - Room @Dao with CRUD
- [ ]   - `data/local/entity/NewsItemEntity.kt` - Room @Entity
- [ ]   - `data/local/dao/NewsItemDao.kt` - Room @Dao with CRUD
- [ ]   - `data/local/entity/ChurchProfileEntity.kt` - Room @Entity
- [ ]   - `data/local/dao/ChurchProfileDao.kt` - Room @Dao with CRUD
- [ ]   - Store timestamps as Long (not Instant) in entities
- [ ] **Create Repositories (with STATE MANAGEMENT):**
- [ ]   - [ ] `data/repositories/churchprofile/ChurchProfileRepository.kt`
- [ ]   - [ ] `data/repositories/event/EventRepository.kt`
- [ ]   - [ ] `data/repositories/newsitem/NewsItemRepository.kt`
- [ ]   - Each repository holds `MutableStateFlow` for its data
- [ ]   - Exposes `StateFlow` to ViewModels (read-only)
- [ ]   - Implements offline-first: cache on fetch, serve from cache when offline

---

## Phase 4: Use Cases

**Load these skills using the Skill tool:**
- `Skill(skill="data-skill")`

**Description:** Create use case classes for business logic

**Tasks:**
- [ ] **Create Use Cases in `domain/usecase/`:**
- [ ]   - `GetUpcomingEventsUseCase.kt` (feature: Event Calendar)
- [ ]   - `FilterEventsByCategoryUseCase.kt` (feature: Event Calendar)
- [ ]   - `SearchEventsUseCase.kt` (feature: Event Calendar)
- [ ]   - `GetLatestNewsUseCase.kt` (feature: News Feed)
- [ ]   - `MarkNewsAsReadUseCase.kt` (feature: News Feed)
- [ ]   - `LoadNewsByDateRangeUseCase.kt` (feature: News Feed)
- [ ]   - `FetchEventLocationUseCase.kt` (feature: Event Details)
- [ ]   - `GetEventDescriptionUseCase.kt` (feature: Event Details)
- [ ]   - `OpenMapForLocationUseCase.kt` (feature: Event Details)
- [ ]   - `ApplyCategoryFilterUseCase.kt` (feature: Category Filtering)
- [ ]   - `SavePreferredCategoriesUseCase.kt` (feature: Category Filtering)
- [ ]   - `ResetFiltersUseCase.kt` (feature: Category Filtering)
- [ ]   - `SetGoogleSheetUrlUseCase.kt` (feature: Admin Configuration)
- [ ]   - `TestSheetConnectionUseCase.kt` (feature: Admin Configuration)
- [ ]   - `UpdateChurchProfileUseCase.kt` (feature: Admin Configuration)
- [ ] Each UseCase: single `operator fun invoke()` or `suspend operator fun invoke()`
- [ ] UseCase calls Repository, applies business logic, returns result
- [ ] Keep UseCases focused - one responsibility each

---

## Phase 5: ViewModels

**Load these skills using the Skill tool:**
- `Skill(skill="feature-orchestration-skill")`
- `Skill(skill="coroutine-flow-skill")`

**Description:** Create THIN ViewModels that observe repository state

**Tasks:**
- [ ] **Create ViewModels (feature-based organization):**
- [ ]   - [ ] `presentation/adminconfig/AdminConfigViewModel.kt` with `AdminConfigUiState` sealed interface
- [ ]   - [ ] `presentation/churchprofile/ChurchProfileViewModel.kt` with `ChurchProfileUiState` sealed interface
- [ ]   - [ ] `presentation/eventcalendar/EventCalendarViewModel.kt` with `EventCalendarUiState` sealed interface
- [ ]   - [ ] `presentation/eventcategoryfilter/EventCategoryFilterViewModel.kt` with `EventCategoryFilterUiState` sealed interface
- [ ]   - [ ] `presentation/eventdetail/EventDetailViewModel.kt` with `EventDetailUiState` sealed interface
- [ ]   - [ ] `presentation/newsdetail/NewsDetailViewModel.kt` with `NewsDetailUiState` sealed interface
- [ ]   - [ ] `presentation/newsfeed/NewsFeedViewModel.kt` with `NewsFeedUiState` sealed interface
- [ ]   - [ ] `presentation/onboarding/OnboardingViewModel.kt` with `OnboardingUiState` sealed interface
- [ ]   - [ ] `presentation/settings/SettingsViewModel.kt` with `SettingsUiState` sealed interface
- [ ] **UiState Pattern:**
- [ ]   - `sealed interface XxxUiState { Loading, Success(data), Error(message) }`
- [ ]   - ViewModel exposes `val uiState: StateFlow<XxxUiState>`
- [ ] **THIN ViewModel Pattern:**
- [ ]   - Observe repository StateFlow, transform to UiState
- [ ]   - NO business logic in ViewModel - delegate to UseCases
- [ ]   - Use `viewModelScope.launch` for coroutines

---

## Phase 6: Screens

**Load these skills using the Skill tool:**
- `Skill(skill="ui-skill")`

**Description:** Create screen composables with proper state handling

**Tasks:**
- [ ] **Create Screens (feature-based organization):**
- [ ]   - [ ] `presentation/adminconfig/AdminConfigScreen.kt` using `AdminConfigViewModel`
- [ ]   - [ ] `presentation/adminconfig/AdminConfigUiState.kt` sealed interface
- [ ]   - [ ] `presentation/churchprofile/ChurchProfileScreen.kt` using `ChurchProfileViewModel`
- [ ]   - [ ] `presentation/churchprofile/ChurchProfileUiState.kt` sealed interface
- [ ]   - [ ] `presentation/eventcalendar/EventCalendarScreen.kt` using `EventCalendarViewModel`
- [ ]   - [ ] `presentation/eventcalendar/EventCalendarUiState.kt` sealed interface
- [ ]   - [ ] `presentation/eventcategoryfilter/EventCategoryFilterScreen.kt` using `EventCategoryFilterViewModel`
- [ ]   - [ ] `presentation/eventcategoryfilter/EventCategoryFilterUiState.kt` sealed interface
- [ ]   - [ ] `presentation/eventdetail/EventDetailScreen.kt` using `EventDetailViewModel`
- [ ]   - [ ] `presentation/eventdetail/EventDetailUiState.kt` sealed interface
- [ ]   - [ ] `presentation/newsdetail/NewsDetailScreen.kt` using `NewsDetailViewModel`
- [ ]   - [ ] `presentation/newsdetail/NewsDetailUiState.kt` sealed interface
- [ ]   - [ ] `presentation/newsfeed/NewsFeedScreen.kt` using `NewsFeedViewModel`
- [ ]   - [ ] `presentation/newsfeed/NewsFeedUiState.kt` sealed interface
- [ ]   - [ ] `presentation/onboarding/OnboardingScreen.kt` using `OnboardingViewModel`
- [ ]   - [ ] `presentation/onboarding/OnboardingUiState.kt` sealed interface
- [ ]   - [ ] `presentation/settings/SettingsScreen.kt` using `SettingsViewModel`
- [ ]   - [ ] `presentation/settings/SettingsUiState.kt` sealed interface
- [ ] **Screen Pattern:**
- [ ]   - `@Composable fun XxxScreen(viewModel: XxxViewModel = koinViewModel())`
- [ ]   - Collect state: `val uiState by viewModel.uiState.collectAsState()`
- [ ]   - Handle Loading/Success/Error states with `when(uiState)`
- [ ]   - Use `koinViewModel()` as DEFAULT parameter (never pass null)

---

## Phase 7: Navigation

**Load these skills using the Skill tool:**
- `Skill(skill="ui-skill")`

**Description:** Set up type-safe navigation with @Serializable routes

**Tasks:**
- [ ] **Create Navigation in `navigation/`:**
- [ ]   - `NavRoutes.kt` - @Serializable route classes (NOT string routes)
- [ ]   - `NavigationHost.kt` - NavHost with composable<Route> entries
- [ ]   - Start destination: `HomeScreen`
- [ ] **Navigation Flows:**
- [ ]   - HomeScreen -> NewsFeedScreen (tap 'Latest News')
- [ ]   - HomeScreen -> EventCalendarScreen (tap 'Upcoming Events')
- [ ]   - HomeScreen -> ChurchProfileScreen (tap 'About Us')
- [ ]   - ChurchProfileScreen -> SetupScreen (tap 'Update Sheet')
- [ ]   - SetupScreen -> HomeScreen (after successful sheet load)
- [ ]   - ... (+1 more flows)
- [ ] **Type-Safe Navigation:**
- [ ]   - `@Serializable object Home` for simple routes
- [ ]   - `@Serializable data class Detail(val id: String)` for parameterized routes
- [ ]   - Navigate: `navController.navigate(Detail(id = "123"))`
- [ ] For setup screens: check existing state in ViewModel init (skip if data exists)

---

## Phase 8: Dependency Injection

**Load these skills using the Skill tool:**
- `Skill(skill="koin-di-skill")`

**Description:** Register all classes in Koin (now that they all exist)

**Tasks:**
- [ ] **Create Koin Modules in `di/`:**
- [ ]   - `AppModule.kt` - main module with all registrations
- [ ]   - `expect fun platformModule(): Module` in commonMain
- [ ]   - `actual fun platformModule(): Module` in androidMain/iosMain
- [ ] 
- [ ] **Register ALL classes created in previous phases:**
- [ ] *Repositories:*
- [ ]   - `singleOf(::ChurchProfileRepository)`
- [ ]   - `singleOf(::EventRepository)`
- [ ]   - `singleOf(::NewsItemRepository)`
- [ ] *UseCases:*
- [ ]   - `factoryOf(::GetUpcomingEventsUseCase)`
- [ ]   - `factoryOf(::FilterEventsByCategoryUseCase)`
- [ ]   - `factoryOf(::SearchEventsUseCase)`
- [ ]   - `factoryOf(::GetLatestNewsUseCase)`
- [ ]   - `factoryOf(::MarkNewsAsReadUseCase)`
- [ ]   - `factoryOf(::LoadNewsByDateRangeUseCase)`
- [ ]   - `factoryOf(::FetchEventLocationUseCase)`
- [ ]   - `factoryOf(::GetEventDescriptionUseCase)`
- [ ]   - `factoryOf(::OpenMapForLocationUseCase)`
- [ ]   - `factoryOf(::ApplyCategoryFilterUseCase)`
- [ ]   - `factoryOf(::SavePreferredCategoriesUseCase)`
- [ ]   - `factoryOf(::ResetFiltersUseCase)`
- [ ]   - `factoryOf(::SetGoogleSheetUrlUseCase)`
- [ ]   - `factoryOf(::TestSheetConnectionUseCase)`
- [ ]   - `factoryOf(::UpdateChurchProfileUseCase)`
- [ ] *ViewModels:*
- [ ]   - `viewModelOf(::AdminConfigViewModel)`
- [ ]   - `viewModelOf(::ChurchProfileViewModel)`
- [ ]   - `viewModelOf(::EventCalendarViewModel)`
- [ ]   - `viewModelOf(::EventCategoryFilterViewModel)`
- [ ]   - `viewModelOf(::EventDetailViewModel)`
- [ ]   - `viewModelOf(::NewsDetailViewModel)`
- [ ]   - `viewModelOf(::NewsFeedViewModel)`
- [ ]   - `viewModelOf(::OnboardingViewModel)`
- [ ]   - `viewModelOf(::SettingsViewModel)`
- [ ] 
- [ ] **Platform-Specific Dependencies:**
- [ ]   - Use Interface + platformModule injection (NOT expect object)
- [ ]   - Example: `interface DateFormatter` → `AndroidDateFormatter` / `IosDateFormatter`

---

## Phase 9: Review & Fix

**Load these skills using the Skill tool:**
- `Skill(skill="validation-skill")`

**Description:** Review all phases and fix potential issues before GitHub Actions build

**Tasks:**
- [ ] **Review Each Phase for Common Issues:**
- [ ] 
- [ ] *Theme:*
- [ ]   - Colors match ui_design specifications
- [ ]   - Dark mode properly toggles if enabled
- [ ] 
- [ ] *Domain Models:*
- [ ]   - Using `kotlin.time.Instant` (NOT kotlinx.datetime)
- [ ]   - All fields have sensible defaults
- [ ] 
- [ ] *Data Layer:*
- [ ]   - Repositories hold StateFlow (state management)
- [ ]   - Timestamps stored as Long in Room entities
- [ ]   - Error handling returns empty list, not crash
- [ ] 
- [ ] *ViewModels:*
- [ ]   - THIN pattern - observe repository, no business logic
- [ ]   - UiState sealed interface with Loading/Success/Error
- [ ] 
- [ ] *Screens:*
- [ ]   - `koinViewModel()` as default parameter
- [ ]   - Handles all UiState branches
- [ ] 
- [ ] *Navigation:*
- [ ]   - @Serializable route classes (not strings)
- [ ]   - Icons use `Icons.AutoMirrored.Filled` for arrows/lists
- [ ] 
- [ ] *DI:*
- [ ]   - ALL ViewModels registered with `viewModelOf()`
- [ ]   - ALL Repositories registered with `singleOf()`
- [ ] 
- [ ] **Final Tasks:**
- [ ]   - Update README.md with app name, description, features from project-context.json
- [ ]   - Remove any placeholder comments
- [ ]   - Ensure no empty folders remain

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
