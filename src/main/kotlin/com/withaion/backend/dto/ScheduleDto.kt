package com.withaion.backend.dto

import com.withaion.backend.models.Event
import com.withaion.backend.models.Location
import com.withaion.backend.models.RepeatType
import com.withaion.backend.models.Schedule
import java.time.LocalDateTime

/**
 * DTO for scheduled event responses
 *
 * @property startDateTime - Start date and time of scheduled event
 * @property endDateTime - End date and time of scheduled event
 * @property repeat - Repeating type of recurring event
 * @property until - Until date if event is recurring
 * @property location - Location id of scheduled event
 * @property event - Event id where schedule belong to
 * */
class ScheduleNewDto(
        val startDateTime: LocalDateTime,
        val endDateTime: LocalDateTime,
        val until: LocalDateTime?,
        val repeat: RepeatType = RepeatType.NONE,
        val location: String,
        val event: String
) {

    /**
     * toSchedule
     *
     * Create schedule object based data from this dto object
     *
     * @param location - Location of the new schedule
     * @param event - Event of the new schedule
     */
    fun toSchedule(location: Location, event: Event): Schedule {
        return Schedule(startDateTime = startDateTime, endDateTime = endDateTime, until = until, repeatType = repeat, location = location, event = event)
    }
}
