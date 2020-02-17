package com.withaion.backend.dto

import com.withaion.backend.models.*
import java.time.LocalDateTime

class RescheduleRequestDto(
        val oldDateTime: LocalDateTime,
        val newDateTime: LocalDateTime,
        val type: RescheduleType,
        val event: String
) {
    fun toReschedule(event: Event, schedule: Schedule, requestedBy: User): Reschedule {
        return Reschedule(event = event, schedule = schedule, oldDateTime = oldDateTime, newDateTime = newDateTime, type = type, requestedBy = requestedBy)
    }
}