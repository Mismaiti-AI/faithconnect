package com.faithconnect.presentation.newsfeed

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.faithconnect.domain.model.NewsItem
import com.faithconnect.presentation.components.EmptyView
import com.faithconnect.presentation.components.ErrorView
import com.faithconnect.presentation.components.LoadingView
import kotlin.time.ExperimentalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun NewsFeedScreen(
    viewModel: NewsFeedViewModel = koinViewModel(),
    onNewsClick: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News Feed") },
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
                is NewsFeedUiState.Loading -> {
                    LoadingView()
                }
                is NewsFeedUiState.Success -> {
                    NewsFeedContent(
                        state = state,
                        onNewsClick = onNewsClick,
                        onRefresh = { viewModel.refresh() }
                    )
                }
                is NewsFeedUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = { viewModel.loadNews() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun NewsFeedContent(
    state: NewsFeedUiState.Success,
    onNewsClick: (String) -> Unit,
    onRefresh: () -> Unit
) {
    if (state.newsItems.isEmpty()) {
            EmptyView(message = "No news available")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.newsItems, key = { it.id }) { newsItem ->
                    NewsCard(
                        newsItem = newsItem,
                        onClick = { onNewsClick(newsItem.id) }
                    )
                }
            }
        }
    }

@OptIn(ExperimentalTime::class)
@Composable
private fun NewsCard(
    newsItem: NewsItem,
    onClick: () -> Unit
) {
    val cardModifier = if (newsItem.isUrgent) {
        Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.error,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
    } else {
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    }

    Card(
        modifier = cardModifier,
        colors = if (newsItem.isUrgent) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Urgent badge
            if (newsItem.isUrgent) {
                AssistChip(
                    onClick = {},
                    label = { Text("URGENT") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        labelColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.height(28.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Headline
            Text(
                text = newsItem.headline,
                style = MaterialTheme.typography.titleMedium,
                color = if (newsItem.isUrgent) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Author and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (newsItem.author.isNotEmpty()) {
                    Text(
                        text = "By ${newsItem.author}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (newsItem.isUrgent) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                val localDate = newsItem.publishDate.toLocalDateTime(TimeZone.currentSystemDefault())
                Text(
                    text = "${localDate.dayOfMonth}/${localDate.monthNumber}/${localDate.year}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (newsItem.isUrgent) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            // Body excerpt
            if (newsItem.body.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = newsItem.body.take(150) + if (newsItem.body.length > 150) "..." else "",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    color = if (newsItem.isUrgent) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            // Category
            if (newsItem.category.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                AssistChip(
                    onClick = {},
                    label = { Text(newsItem.category) },
                    modifier = Modifier.height(28.dp)
                )
            }
        }
    }
}
