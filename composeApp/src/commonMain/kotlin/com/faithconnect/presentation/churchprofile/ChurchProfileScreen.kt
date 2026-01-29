package com.faithconnect.presentation.churchprofile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.faithconnect.domain.model.ChurchProfile
import com.faithconnect.presentation.components.ErrorView
import com.faithconnect.presentation.components.LoadingView
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChurchProfileScreen(
    viewModel: ChurchProfileViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToSetup: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var isEditMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Church Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState is ChurchProfileUiState.Success) {
                        IconButton(onClick = { isEditMode = !isEditMode }) {
                            Icon(
                                if (isEditMode) Icons.Default.Save else Icons.Default.Edit,
                                contentDescription = if (isEditMode) "Save" else "Edit"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is ChurchProfileUiState.Loading -> {
                    LoadingView()
                }
                is ChurchProfileUiState.Success -> {
                    if (state.profile != null) {
                        ChurchProfileContent(
                            profile = state.profile,
                            isEditMode = isEditMode,
                            isRefreshing = state.isRefreshing,
                            onRefresh = { viewModel.refresh() },
                            onSave = { updatedProfile ->
                                viewModel.updateProfile(updatedProfile)
                                isEditMode = false
                            },
                            onNavigateToSetup = onNavigateToSetup
                        )
                    } else {
                        EmptyProfileView(onNavigateToSetup = onNavigateToSetup)
                    }
                }
                is ChurchProfileUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = { viewModel.loadProfile() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChurchProfileContent(
    profile: ChurchProfile,
    isEditMode: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onSave: (ChurchProfile) -> Unit,
    onNavigateToSetup: () -> Unit
) {
    var editedProfile by remember(profile) { mutableStateOf(profile) }
    val scrollState = rememberScrollState()

    val pullRefreshState = rememberPullToRefreshState()
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
        }
    }

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            pullRefreshState.endRefresh()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isEditMode) {
                // Edit Mode
                OutlinedTextField(
                    value = editedProfile.name,
                    onValueChange = { editedProfile = editedProfile.copy(name = it) },
                    label = { Text("Church Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editedProfile.welcomeMessage,
                    onValueChange = { editedProfile = editedProfile.copy(welcomeMessage = it) },
                    label = { Text("Welcome Message") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                OutlinedTextField(
                    value = editedProfile.address,
                    onValueChange = { editedProfile = editedProfile.copy(address = it) },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editedProfile.phone,
                    onValueChange = { editedProfile = editedProfile.copy(phone = it) },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editedProfile.email,
                    onValueChange = { editedProfile = editedProfile.copy(email = it) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editedProfile.website,
                    onValueChange = { editedProfile = editedProfile.copy(website = it) },
                    label = { Text("Website") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editedProfile.mission,
                    onValueChange = { editedProfile = editedProfile.copy(mission = it) },
                    label = { Text("Mission Statement") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                OutlinedTextField(
                    value = editedProfile.serviceTimes,
                    onValueChange = { editedProfile = editedProfile.copy(serviceTimes = it) },
                    label = { Text("Service Times") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editedProfile.socialFacebook,
                    onValueChange = { editedProfile = editedProfile.copy(socialFacebook = it) },
                    label = { Text("Facebook URL") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { onSave(editedProfile) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            } else {
                // View Mode
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        if (profile.welcomeMessage.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = profile.welcomeMessage,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (profile.mission.isNotEmpty()) {
                    ProfileInfoCard(
                        title = "Mission",
                        content = profile.mission
                    )
                }

                if (profile.address.isNotEmpty()) {
                    ProfileInfoCard(
                        title = "Address",
                        content = profile.address
                    )
                }

                if (profile.serviceTimes.isNotEmpty()) {
                    ProfileInfoCard(
                        title = "Service Times",
                        content = profile.serviceTimes
                    )
                }

                if (profile.phone.isNotEmpty()) {
                    ProfileInfoCard(
                        title = "Phone",
                        content = profile.phone
                    )
                }

                if (profile.email.isNotEmpty()) {
                    ProfileInfoCard(
                        title = "Email",
                        content = profile.email
                    )
                }

                if (profile.website.isNotEmpty()) {
                    ProfileInfoCard(
                        title = "Website",
                        content = profile.website
                    )
                }

                if (profile.socialFacebook.isNotEmpty()) {
                    ProfileInfoCard(
                        title = "Facebook",
                        content = profile.socialFacebook
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToSetup,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Sheet URL")
                }
            }
        }

        PullToRefreshContainer(
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun ProfileInfoCard(
    title: String,
    content: String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun EmptyProfileView(
    onNavigateToSetup: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Church Profile Found",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Configure your Google Sheets URL to load your church profile.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToSetup) {
            Text("Go to Setup")
        }
    }
}
