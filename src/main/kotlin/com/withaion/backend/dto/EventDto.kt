package com.withaion.backend.dto

import com.withaion.backend.models.Event
import com.withaion.backend.models.RepeatType
import java.time.LocalDateTime

class EventNewDto(
        val name: String,
        val description: String?,
        val startDateTime: LocalDateTime,
        val endDateTime: LocalDateTime,
        val repeat: RepeatType = RepeatType.NONE
) {
    fun toEvent(): Event {
        return Event(name = name, description = description, startDateTime = startDateTime, endDateTime = endDateTime, repeat = repeat)
    }
}

class EventUpdateDto(
        val name: String?,
        val description: String?,
        val startDateTime: LocalDateTime?,
        val endDateTime: LocalDateTime?,
        val repeat: RepeatType?
) {
    fun toUpdatedEvent(event: Event): Event {
        var currentEvent = event
        name?.let {
            currentEvent = currentEvent.copy(name = name)
        }
        description?.let {
            currentEvent = currentEvent.copy(description = description)
        }
        startDateTime?.let {
            currentEvent = currentEvent.copy(startDateTime = startDateTime)
        }
        endDateTime?.let {
            currentEvent = currentEvent.copy(endDateTime = endDateTime)
        }
        repeat?.let {
            currentEvent = currentEvent.copy(repeat = repeat)
        }
        return currentEvent
    }
}