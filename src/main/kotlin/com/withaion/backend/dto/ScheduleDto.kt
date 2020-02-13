package com.withaion.backend.dto

import com.withaion.backend.models.Event
import com.withaion.backend.models.Location
import com.withaion.backend.models.RepeatType
import com.withaion.backend.models.Schedule
import java.time.LocalDateTime

class ScheduleNewDto(
        val startDateTime: LocalDateTime,
        val endDateTime: LocalDateTime,
        val until: LocalDateTime?,
        val repeat: RepeatType = RepeatType.NONE,
        val location: String,
        val event: String
) {
    fun toSchedule(location: Location, event: Event): Schedule {
        return Schedule(startDateTime = startDateTime, endDateTime = endDateTime, until = until, repeatType = repeat, location = location, event = event)
    }
}
