# FaithConnect

A Kotlin Multiplatform Mobile App for Church Community Management

## Overview

FaithConnect helps congregation members stay connected with church activities, special events like seminars or workshops, news, and articles. Built with modern Compose Multiplatform technology, it provides a seamless experience across Android and iOS platforms.

## Features

### Event Calendar
Browse upcoming seminars, workshops, and church activities with date, time, and category filtering. View detailed information for each event including location, description, speaker, and registration details.

### News Feed
Read church announcements, pastoral updates, and community highlights. Stay informed about important church news with categorized articles and scripture references.

### Event Details
View full details for any event including location, description, speaker, and registration information. Open location in maps for easy navigation.

### Category Filtering
Filter events and news by ministry or audience (e.g., Youth, Women's Ministry, Outreach). Save preferred categories for quick access.

### Admin Configuration
Church administrators can configure and manage their FaithConnect instance by setting the Google Sheet URL, validating connectivity, and updating church profile data.

## Tech Stack

- **Framework:** Kotlin Multiplatform (KMP) with Compose Multiplatform
- **Architecture:** MVVM with Clean Architecture
- **State Management:** Kotlin Flow and StateFlow
- **Dependency Injection:** Koin
- **Local Database:** Room (SQLite)
- **Backend:** Google Sheets (via CSV export)
- **Navigation:** Type-safe navigation with @Serializable routes
- **Platforms:** Android & iOS

## Architecture

### Layers

1. **Presentation Layer** - Compose UI screens, ViewModels, and UI state
2. **Domain Layer** - Business logic, use cases, and domain models
3. **Data Layer** - Repositories, data sources (Room, Google Sheets)

### Key Patterns

- **THIN ViewModels:** ViewModels observe repository state, delegate business logic to use cases
- **Repository State Management:** Shared state lives in repositories using StateFlow
- **Type-Safe Navigation:** @Serializable route classes for compile-time safety
- **Offline-First:** Local caching with Room for offline access

## Project Structure

```
composeApp/src/
├── commonMain/          # Shared code for all platforms
│   ├── domain/          # Business logic & models
│   │   ├── model/       # Domain models (Event, NewsItem, ChurchProfile)
│   │   ├── repository/  # Repository interfaces
│   │   └── usecase/     # Use cases
│   ├── data/            # Data layer implementation
│   │   ├── local/       # Room database & DAOs
│   │   ├── remote/      # Google Sheets service
│   │   └── repository/  # Repository implementations
│   ├── presentation/    # UI layer
│   │   ├── home/        # Home screen
│   │   ├── events/      # Event calendar & details
│   │   ├── news/        # News feed & details
│   │   └── ...          # Other screens
│   └── di/              # Dependency injection modules
├── androidMain/         # Android-specific code
└── iosMain/             # iOS-specific code
```

## Getting Started

### Prerequisites

- JDK 17 or higher
- Android Studio Ladybug or later
- Xcode 15+ (for iOS development)
- Kotlin 2.1.0+

### Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. For iOS: Open `iosApp/iosApp.xcodeproj` in Xcode

### Configuration

#### Google Sheets Setup

1. Create a Google Sheet with tabs for:
   - Events (columns: id, title, date, category, location, description, topic, bibleVerse, picCouncilMember, onDutyCouncilMembers, isFeatured)
   - News (columns: id, headline, publishDate, author, body, category, scriptureReference, isUrgent, photoURL, relatedEventId)
   - ChurchProfile (columns: name, logoURL, welcomeMessage, address, phone, website, email, mission, serviceTimes, socialFacebook)

2. Publish the sheet (File > Share > Publish to web > CSV)
3. Copy the published URL
4. On first app launch, enter the Google Sheet URL in the onboarding screen

## Data Models

### Event
- Event calendar entries with date, time, location, and category
- Support for featured events and Bible verse references
- Council member assignments

### NewsItem
- Church news and announcements
- Author and publish date tracking
- Scripture references and urgency flags
- Photo attachments and related events

### ChurchProfile
- Church information and branding
- Contact details and service times
- Mission statement and social media links

## Development

### Building

```bash
# Android
./gradlew assembleDebug

# iOS
cd iosApp && xcodebuild -scheme iosApp -configuration Debug
```

### Testing

```bash
# Run unit tests
./gradlew testDebugUnitTest
```

## Critical Code Rules

1. **Date/Time:** Always use `kotlin.time.Instant`, never `kotlinx.datetime.Instant`
2. **ViewModel Registration:** Register all ViewModels in Koin with `viewModelOf()`
3. **ViewModel Injection:** Use `koinViewModel()` as default parameter in screens
4. **Navigation:** Use `@Serializable` route classes, not string-based routes
5. **Icons:** Use `Icons.AutoMirrored.Filled` for directional icons (arrows, lists)
6. **State Management:** Repositories hold StateFlow, ViewModels observe (don't copy)
7. **Timestamps:** Store as Long in Room entities, convert to/from Instant in mappers

## License

Copyright © 2026 Mismaiti. All rights reserved.