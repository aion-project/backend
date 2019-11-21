package com.withaion.backend.dto

import com.withaion.backend.models.Event
import com.withaion.backend.models.RepeatType
import java.time.LocalDateTime

class EventUpdateDto(
        val name: String?,
        val description: String?,
        val startDateTime: LocalDateTime?,
        val endDateTime: LocalDateTime?,
        val repeat: RepeatType?
) {
    fun toUpdatedEvent(event: Event): Event {
        var currentGroup = event
        name?.let {
            currentGroup = currentGroup.copy(name = name)
        }
        description?.let {
            currentGroup = currentGroup.copy(description = description)
        }
        startDateTime?.let {
            currentGroup = currentGroup.copy(startDateTime = startDateTime)
        }
        endDateTime?.let {
            currentGroup = currentGroup.copy(endDateTime = endDateTime)
        }
        repeat?.let {
            currentGroup = currentGroup.copy(repeat = repeat)
        }
        return currentGroup
    }
}


