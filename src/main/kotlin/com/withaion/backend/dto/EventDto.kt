package com.withaion.backend.dto

import com.withaion.backend.models.Event
import com.withaion.backend.models.RepeatType
import com.withaion.backend.models.Schedule
import java.time.LocalDateTime

class EventNewDto(
        val name: String,
        val description: String?,
        val startDateTime: LocalDateTime,
        val endDateTime: LocalDateTime,
        val repeat: RepeatType = RepeatType.NONE
) {
    fun getSchedule(): Schedule {
        return Schedule(startDateTime = startDateTime, endDateTime = endDateTime, repeatType = repeat)
    }

    fun toEvent(schedule: List<Schedule> = listOf()): Event {
        return Event(name = name, description = description, schedules = schedule)
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