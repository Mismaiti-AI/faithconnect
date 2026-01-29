package com.faithconnect.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.faithconnect.domain.model.Event
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "date")
    val dateMillis: Long,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "location")
    val location: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "topic")
    val topic: String,

    @ColumnInfo(name = "bible_verse")
    val bibleVerse: String,

    @ColumnInfo(name = "pic_council_member")
    val picCouncilMember: String,

    @ColumnInfo(name = "on_duty_council_members")
    val onDutyCouncilMembersJson: String,

    @ColumnInfo(name = "is_featured")
    val isFeatured: Boolean
)

// Mapper: Entity to Domain
@OptIn(ExperimentalTime::class)
fun EventEntity.toDomain(): Event {
    val onDutyList = try {
        if (onDutyCouncilMembersJson.isBlank()) {
            emptyList()
        } else {
            Json.decodeFromString<List<String>>(onDutyCouncilMembersJson)
        }
    } catch (e: Exception) {
        emptyList()
    }

    return Event(
        id = id,
        title = title,
        date = Instant.fromEpochMilliseconds(dateMillis),
        category = category,
        location = location,
        description = description,
        topic = topic,
        bibleVerse = bibleVerse,
        picCouncilMember = picCouncilMember,
        onDutyCouncilMembers = onDutyList,
        isFeatured = isFeatured
    )
}

// Mapper: Domain to Entity
@OptIn(ExperimentalTime::class)
fun Event.toEntity(): EventEntity {
    return EventEntity(
        id = id,
        title = title,
        dateMillis = date.toEpochMilliseconds(),
        category = category,
        location = location,
        description = description,
        topic = topic,
        bibleVerse = bibleVerse,
        picCouncilMember = picCouncilMember,
        onDutyCouncilMembersJson = Json.encodeToString(onDutyCouncilMembers),
        isFeatured = isFeatured
    )
}
