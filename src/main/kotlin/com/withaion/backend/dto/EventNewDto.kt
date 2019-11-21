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
