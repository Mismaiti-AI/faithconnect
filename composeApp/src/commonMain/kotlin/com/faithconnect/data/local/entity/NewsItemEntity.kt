package com.faithconnect.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.faithconnect.domain.model.NewsItem
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Entity(tableName = "news_items")
data class NewsItemEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "headline")
    val headline: String,

    @ColumnInfo(name = "publish_date")
    val publishDateMillis: Long,

    @ColumnInfo(name = "author")
    val author: String,

    @ColumnInfo(name = "body")
    val body: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "scripture_reference")
    val scriptureReference: String,

    @ColumnInfo(name = "is_urgent")
    val isUrgent: Boolean,

    @ColumnInfo(name = "photo_url")
    val photoURL: String,

    @ColumnInfo(name = "related_event_id")
    val relatedEventId: String
)

// Mapper: Entity to Domain
@OptIn(ExperimentalTime::class)
fun NewsItemEntity.toDomain(): NewsItem {
    return NewsItem(
        id = id,
        headline = headline,
        publishDate = Instant.fromEpochMilliseconds(publishDateMillis),
        author = author,
        body = body,
        category = category,
        scriptureReference = scriptureReference,
        isUrgent = isUrgent,
        photoURL = photoURL,
        relatedEventId = relatedEventId
    )
}

// Mapper: Domain to Entity
@OptIn(ExperimentalTime::class)
fun NewsItem.toEntity(): NewsItemEntity {
    return NewsItemEntity(
        id = id,
        headline = headline,
        publishDateMillis = publishDate.toEpochMilliseconds(),
        author = author,
        body = body,
        category = category,
        scriptureReference = scriptureReference,
        isUrgent = isUrgent,
        photoURL = photoURL,
        relatedEventId = relatedEventId
    )
}
