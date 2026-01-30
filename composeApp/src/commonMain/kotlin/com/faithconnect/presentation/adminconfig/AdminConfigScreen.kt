package com.faithconnect.presentation.adminconfig

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.faithconnect.presentation.components.LoadingView
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminConfigScreen(
    viewModel: AdminConfigViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
    onConfigSaved: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Configuration") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is AdminConfigUiState.Loading -> {
                    LoadingView()
                }
                is AdminConfigUiState.Success -> {
                    AdminConfigContent(
                        state = state,
                        onSheetUrlChange = { viewModel.setSheetUrl(it) },
                        onTestConnection = { viewModel.testConnection() },
                        onSave = {
                            viewModel.saveConfig()
                            // Check if save was successful (no error)
                            if (state.error == null && !state.isSaving) {
                                onConfigSaved()
                            }
                        },
                        onClearError = { viewModel.clearError() }
                    )
                }
                is AdminConfigUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminConfigContent(
    state: AdminConfigUiState.Success,
    onSheetUrlChange: (String) -> Unit,
    onTestConnection: () -> Unit,
    onSave: () -> Unit,
    onClearError: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Configure Google Sheets URL",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Enter the URL of your Google Sheets document to sync church data.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = state.sheetUrl,
            onValueChange = onSheetUrlChange,
            label = { Text("Google Sheets URL") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isTestingConnection && !state.isSaving,
            isError = !state.isValidUrl && state.sheetUrl.isNotEmpty(),
            supportingText = {
                if (!state.isValidUrl && state.sheetUrl.isNotEmpty()) {
                    Text("Please enter a valid Google Sheets URL")
                }
            }
        )

        // Connection Test Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when (state.connectionTestResult) {
                    is ConnectionTestResult.Success -> MaterialTheme.colorScheme.primaryContainer
                    is ConnectionTestResult.Failure -> MaterialTheme.colorScheme.errorContainer
                    null -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Connection Test",
                    style = MaterialTheme.typography.titleMedium
                )

                when (val result = state.connectionTestResult) {
                    is ConnectionTestResult.Success -> {
                        Text(
                            text = "Connection successful! The sheet is accessible.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    is ConnectionTestResult.Failure -> {
                        Text(
                            text = "Connection failed: ${result.message}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    null -> {
                        Text(
                            text = "Test the connection to verify the sheet is accessible.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Button(
            onClick = onTestConnection,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isValidUrl && !state.isTestingConnection && !state.isSaving
        ) {
            if (state.isTestingConnection) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Testing Connection...")
            } else {
                Text("Test Connection")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isValidUrl && !state.isTestingConnection && !state.isSaving &&
                    state.connectionTestResult is ConnectionTestResult.Success
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Saving Configuration...")
            } else {
                Text("Save Configuration")
            }
        }

        // Error message
        if (state.error != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onClearError) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
}
