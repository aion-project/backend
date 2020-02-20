package com.withaion.backend.dto

import com.withaion.backend.models.*
import java.time.LocalDateTime

/**
 * DTO for event creation  requests
 *
 * @property name - Name of the location
 * @property description - Description of the location
 * */
class EventNewDto(
        val name: String,
        val description: String?
) {
    /**
     * toEvent
     *
     * Utility function to create event object from this dto
     */
    fun toEvent(): Event {
        return Event(name = name, description = description)
    }
}

/**
 * DTO for event update  requests
 *
 * @property name - Name of the location
 * @property description - Description of the location
 * */
class EventUpdateDto(
        val name: String?,
        val description: String?
) {
    /**
     * toUpdatedEvent
     *
     * Utility function to update existing event object with data of this dto
     *
     * @param event - Object of existing event object to be updated
     */
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

/**
 * DTO for scheduled event responses
 *
 * @property eventId - Event id of the scheduled event
 * @property scheduleId - Schedule id of the scheduled event
 * @property name - Name of the location
 * @property startDateTime - Start date and time of scheduled event
 * @property endDateTime - End date and time of scheduled event
 * @property color - Color of scheduled event
 * @property location - Location of scheduled event
 * @property users - Assigned list of users of scheduled event
 * */
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