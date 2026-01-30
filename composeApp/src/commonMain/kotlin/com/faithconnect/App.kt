package com.faithconnect

import androidx.compose.runtime.*
import com.faithconnect.di.appModule
import com.faithconnect.di.databaseModule
import com.faithconnect.di.networkModule
import com.faithconnect.di.platformModule
import com.faithconnect.presentation.navigation.NavigationHost
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinAppDeclaration


@Composable
fun App(koinAppDeclaration: KoinAppDeclaration? = null) {
    KoinApplication(application = {
        // Load all DI modules
        modules(
            appModule,        // Repositories, Use Cases, ViewModels
            networkModule,    // HttpClient, GoogleSheetsService
            databaseModule,   // Room Database, DAOs
            platformModule()  // Platform-specific dependencies (Android/iOS)
        )
        koinAppDeclaration?.invoke(this)
    }) {
        AppTheme {
            NavigationHost()
        }
    }
}
