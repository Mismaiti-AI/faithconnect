package com.faithconnect

import androidx.compose.runtime.*
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinAppDeclaration


@Composable
fun App(koinAppDeclaration: KoinAppDeclaration? = null) {
    KoinApplication(application = {
        modules(listOf())
        koinAppDeclaration?.invoke(this)
    }) {
        AppTheme {
            // TBD: set content here
        }
    }
}
