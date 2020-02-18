package com.withaion.backend.dto

import com.withaion.backend.models.*
import java.time.LocalDateTime

class EventNewDto(
        val name: String,
        val description: String?
) {
    fun toEvent(): Event {
        return Event(name = name, description = description)
    }
}

class EventUpdateDto(
        val name: String?,
        val description: String?
) {
    fun toUpdatedEvent(event: Event): Event {
        var currentEvent = event
        name?.let {
            currentEvent = currentEvent.copy(name = name)
        }
        description?.let {
            currentEvent = currentEvent.copy(description = description)
        }
        return currentEvent
    }
}

class ScheduledEvent(
        val eventId: String,
        val scheduleId: String,
        val name: String,
        val startDateTime: LocalDateTime,
        val endDateTime: LocalDateTime,
        val color: String?,
        val location: Location,
        val users: List<User?> = listOf()
)